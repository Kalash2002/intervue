package si.intervue.content;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.ServiceLoader;
import java.util.function.Function;
import java.util.stream.Stream;
import si.intervue.engine.languages.CompileResult;
import si.intervue.engine.languages.LanguagePackage;
import si.intervue.engine.languages.RunLimits;
import si.intervue.engine.languages.RunRequest;
import si.intervue.engine.runners.HeapPolicy;
import si.intervue.engine.runners.ProcessControl;
import si.intervue.problemformat.ProblemFolder;
import si.intervue.problemformat.ProblemFormatException;
import si.intervue.problemformat.ProblemReader;
import si.intervue.problemformat.ProblemValidator;
import si.intervue.problemformat.Variant;
import si.intervue.runprotocol.RunProtocolException;
import si.intervue.runprotocol.RunProtocolParser;
import si.intervue.runprotocol.RunResults;
import si.intervue.runprotocol.TestResult;
import si.intervue.runprotocol.TestStatus;

/**
 * Verifies a problem the way docs/contracts/problem-format.md requires: the format is valid; the
 * starter fails phase 1; the reference passes everything with 5x headroom under every limit; the
 * naive solution passes every phase but the last and still times out there at 3x the limits (a
 * one-phase question may omit the naive). It never changes the problem folder; all runs happen in a
 * scratch copy.
 */
public final class ProblemVerifier {

  // Same heap as a real session (HeapPolicy): a smaller one slows reference solutions and fails
  // the headroom rule for no real reason.
  private static final RunLimits LIMITS = new RunLimits(900_000, HeapPolicy.defaultMb(), 400_000);
  private static final int HEADROOM = 5;
  private static final int NAIVE_MARGIN = 3;
  private static final int REFERENCE_RUNS = 3;

  private final Path scratch;
  private final double factor;
  private final ProcessControl processes;

  public ProblemVerifier(Path scratch, double factor, ProcessControl processes) {
    this.scratch = scratch;
    this.factor = factor;
    this.processes = processes;
  }

  public VerifyReport verify(Path problemDir) {
    String id = problemDir.getFileName().toString();
    List<String> lines = new ArrayList<>();
    ProblemFolder folder;
    try {
      folder = ProblemReader.read(problemDir);
    } catch (ProblemFormatException e) {
      lines.add("  format     FAIL  " + e.getMessage());
      return new VerifyReport(id, false, lines);
    }
    List<String> errors = ProblemValidator.validate(folder);
    errors.forEach(e -> lines.add("  format     FAIL  " + e));
    boolean ok = errors.isEmpty();
    if ("dsa-quick".equals(folder.problem().section())) {
      // The DSA room starts the main class by this name (ADR 0008); the API does not pass it
      // through.
      for (var entry : folder.variants().entrySet()) {
        if (!"Main".equals(entry.getValue().main())) {
          lines.add(
              "  format     FAIL  dsa-quick variant "
                  + entry.getKey()
                  + " must have \"main\": \"Main\" (found \""
                  + entry.getValue().main()
                  + "\")");
          ok = false;
        }
      }
    }
    for (String language : ok ? folder.problem().languages() : List.<String>of()) {
      ok &= verifyVariant(folder, language, lines);
    }
    return new VerifyReport(id, ok, lines);
  }

  private boolean verifyVariant(ProblemFolder folder, String language, List<String> lines) {
    LanguagePackage pkg = findPackage(language);
    if (pkg == null) {
      lines.add("  " + language + "       FAIL  no language package installed for " + language);
      return false;
    }
    Variant variant = folder.variants().get(language);
    int phases = folder.problem().phases().size();
    String first = variant.tests().get("1");
    String last = variant.tests().get(String.valueOf(phases));
    Path dir = folder.variantDir(language);
    boolean ok =
        check(
            "starter",
            run(pkg, dir, variant, null, "all", factor),
            r -> starterRule(r, first),
            lines);
    Outcome reference = run(pkg, dir, variant, "reference", "all", factor);
    List<RunResults> referenceRuns = new ArrayList<>();
    if (reference.results() != null) {
      referenceRuns.add(reference.results());
    }
    // Re-run only when every test passed but one looks short of headroom; noise adds time.
    while (!referenceRuns.isEmpty()
        && referenceRuns.size() < REFERENCE_RUNS
        && referenceRule(referenceRuns) != null
        && allPassed(referenceRuns.get(0))) {
      Outcome again = run(pkg, dir, variant, "reference", "all", factor);
      if (again.results() == null) {
        break;
      }
      referenceRuns.add(again.results());
    }
    ok &= check("reference", reference, r -> referenceRule(referenceRuns), lines);
    if (!Files.isDirectory(dir.resolve("naive"))) {
      // A one-phase question may have no time dimension worth grading (problem-format.md).
      if (phases == 1) {
        lines.add("  naive      skip  no naive/; time is not graded separately");
        return ok;
      }
      lines.add("  naive      FAIL  missing naive/ with a correct but slow solution");
      return false;
    }
    Outcome naive = run(pkg, dir, variant, "naive", "all", factor);
    ok &= check("naive", naive, r -> naiveRule(r, last, phases == 1), lines);
    if (naive.results() == null) {
      return false;
    }
    Outcome slow = run(pkg, dir, variant, "naive", "phase:" + phases, factor * NAIVE_MARGIN);
    return ok
        & check(
            "naive " + NAIVE_MARGIN + "x",
            slow,
            r -> naiveMarginRule(naive.results(), r, last),
            lines);
  }

  private static LanguagePackage findPackage(String language) {
    for (LanguagePackage pkg : ServiceLoader.load(LanguagePackage.class)) {
      if (pkg.definition().id().equals(language)
          && pkg.definition().spiVersion() == LanguagePackage.SPI_VERSION) {
        return pkg;
      }
    }
    return null;
  }

  /** A run's results, or why there are none (compile error, no readable results file). */
  private record Outcome(RunResults results, String failure, long durationMs) {}

  private Outcome run(
      LanguagePackage pkg,
      Path variantDir,
      Variant variant,
      String overlay,
      String target,
      double runFactor) {
    Path base =
        scratch.resolve(
            variantDir.getParent().getParent().getFileName()
                + "-"
                + (overlay == null ? "starter" : overlay));
    Path work = base.resolve("work");
    Path out = base.resolve("out");
    deleteTree(base);
    copyTree(variantDir.resolve("starter"), work);
    if (overlay != null) {
      copyTree(variantDir.resolve(overlay), work);
    }
    pkg.testKit().install(work);
    long start = System.nanoTime();
    CompileResult compiled = pkg.runner().compile(work, out);
    if (!compiled.ok()) {
      String firstError = compiled.output().lines().skip(2).findFirst().orElse("");
      return new Outcome(null, "did not compile: " + firstError, elapsedMs(start));
    }
    var request = new RunRequest(work, out, target, variant, LIMITS, runFactor);
    var output = pkg.runner().run(request, processes);
    try {
      return new Outcome(RunProtocolParser.parse(output.resultsFile()), null, elapsedMs(start));
    } catch (RunProtocolException e) {
      String said = output.output().lines().findFirst().orElse("");
      return new Outcome(
          null, "no test results (" + e.getMessage() + "); output: " + said, elapsedMs(start));
    }
  }

  private static long elapsedMs(long startNs) {
    return (System.nanoTime() - startNs) / 1_000_000;
  }

  private static String starterRule(RunResults r, String first) {
    return count(r, first, TestStatus.FAIL) + count(r, first, TestStatus.TIMEOUT) > 0
        ? null
        : "the first phase must have a failing test";
  }

  private static boolean allPassed(RunResults r) {
    return r.tests().stream().allMatch(t -> t.status() == TestStatus.PASS)
        && r.suites().stream().allMatch(suite -> r.notRun(suite.name()) == 0);
  }

  /**
   * Every test passes in the first run, and every test's fastest time across the runs fits its
   * limit {@value #HEADROOM} times over. The fastest run is used because noise only adds time.
   */
  static String referenceRule(List<RunResults> runs) {
    RunResults first = runs.get(0);
    if (!allPassed(first)) {
      return "every test must pass";
    }
    for (TestResult t : first.tests()) {
      long fastest =
          runs.stream()
              .flatMap(r -> r.tests().stream())
              .filter(o -> o.suite().equals(t.suite()) && o.name().equals(t.name()))
              .mapToLong(TestResult::ms)
              .min()
              .orElse(t.ms());
      if (t.limitMs() != null && fastest * HEADROOM > t.limitMs()) {
        return t.suite()
            + "."
            + t.name()
            + " took "
            + fastest
            + " ms at best; its "
            + t.limitMs()
            + " ms limit needs "
            + HEADROOM
            + "x headroom";
      }
    }
    return null;
  }

  private static String naiveRule(RunResults r, String last, boolean onePhase) {
    // With one phase there is no earlier phase to pass, so the naive must be correct on every test
    // it does not time out on; a FAIL or an ERROR (crash) would otherwise count as "slow".
    boolean othersPass =
        r.tests().stream()
            .filter(t -> onePhase || !t.suite().equals(last))
            .allMatch(
                t ->
                    t.status() == TestStatus.PASS
                        || (onePhase && t.status() == TestStatus.TIMEOUT));
    return othersPass && count(r, last, TestStatus.TIMEOUT) > 0
        ? null
        : "every phase but the last must pass and the last must have a TIMEOUT";
  }

  /**
   * Every last-phase test the naive solution timed out on must still time out with the limits
   * multiplied by the margin, so a calibration factor between 1 and 4 cannot flip it (PLAN.md 4.4).
   */
  static String naiveMarginRule(RunResults atLimit, RunResults atMargin, String last) {
    for (TestResult t : atLimit.tests()) {
      if (!t.suite().equals(last) || t.status() != TestStatus.TIMEOUT) {
        continue;
      }
      boolean stillSlow =
          atMargin.tests().stream()
              .anyMatch(
                  m ->
                      m.suite().equals(last)
                          && m.name().equals(t.name())
                          && m.status() == TestStatus.TIMEOUT);
      if (!stillSlow) {
        return t.name() + " finishes within " + NAIVE_MARGIN + "x its limit on the naive solution";
      }
    }
    return null;
  }

  private static long count(RunResults r, String suite, TestStatus status) {
    return r.tests().stream().filter(t -> t.suite().equals(suite) && t.status() == status).count();
  }

  /** Prints one line per run, in the prototype's format; a rule returns null when it holds. */
  private static boolean check(
      String label, Outcome outcome, Function<RunResults, String> rule, List<String> lines) {
    String broken = outcome.failure() != null ? null : rule.apply(outcome.results());
    boolean ok = outcome.failure() == null && broken == null;
    StringBuilder line =
        new StringBuilder(
            String.format(
                Locale.ROOT,
                "  %-10s %-5s %6.1f s  ",
                label,
                ok ? "ok" : "FAIL",
                outcome.durationMs() / 1000.0));
    if (outcome.failure() != null) {
      line.append(outcome.failure());
    } else {
      for (var suite : outcome.results().suites()) {
        long passed = count(outcome.results(), suite.name(), TestStatus.PASS);
        long timeouts = count(outcome.results(), suite.name(), TestStatus.TIMEOUT);
        line.append(suite.name()).append(' ').append(passed).append('/').append(suite.tests());
        line.append(timeouts > 0 ? " (" + timeouts + " timeout)" : "").append("  ");
      }
      if (broken != null) {
        line.append("<- ").append(broken);
      }
    }
    lines.add(line.toString());
    return ok;
  }

  private static void copyTree(Path from, Path to) {
    try (Stream<Path> walk = Files.walk(from)) {
      for (Path p : walk.toList()) {
        Path target = to.resolve(from.relativize(p).toString());
        if (Files.isDirectory(p)) {
          Files.createDirectories(target);
        } else {
          Files.copy(p, target, StandardCopyOption.REPLACE_EXISTING);
        }
      }
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  static void deleteTree(Path dir) {
    if (!Files.exists(dir)) {
      return;
    }
    try (Stream<Path> walk = Files.walk(dir)) {
      for (Path p : walk.sorted(Comparator.reverseOrder()).toList()) {
        try {
          Files.deleteIfExists(p);
        } catch (IOException stillInUse) {
          // Windows keeps a file locked for a moment after a killed run; the next run replaces it.
        }
      }
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
