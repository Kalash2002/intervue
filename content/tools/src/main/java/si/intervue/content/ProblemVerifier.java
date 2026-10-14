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
import si.intervue.engine.runners.ProcessControl;
import si.intervue.problemformat.ProblemFolder;
import si.intervue.problemformat.ProblemFormatException;
import si.intervue.problemformat.ProblemReader;
import si.intervue.problemformat.ProblemValidator;
import si.intervue.problemformat.Variant;
import si.intervue.runprotocol.RunProtocolParser;
import si.intervue.runprotocol.RunResults;
import si.intervue.runprotocol.TestResult;
import si.intervue.runprotocol.TestStatus;

/**
 * Verifies a problem the way docs/contracts/problem-format.md requires: the format is valid; the
 * starter fails phase 1; the reference passes everything with 5x headroom under every limit; the
 * naive solution passes every phase but the last and still times out there at 3x the limits. It
 * never changes the problem folder; all runs happen in a scratch copy.
 */
public final class ProblemVerifier {

  private static final RunLimits LIMITS = new RunLimits(900_000, 1024, 400_000);
  private static final int HEADROOM = 5;
  private static final int NAIVE_MARGIN = 3;

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
    ok &=
        check(
            "reference",
            run(pkg, dir, variant, "reference", "all", factor),
            this::referenceRule,
            lines);
    if (!Files.isDirectory(dir.resolve("naive"))) {
      lines.add("  naive      FAIL  missing naive/ with a correct but slow solution");
      return false;
    }
    ok &=
        check(
            "naive",
            run(pkg, dir, variant, "naive", "all", factor),
            r -> naiveRule(r, last),
            lines);
    Outcome slow = run(pkg, dir, variant, "naive", "phase:" + phases, factor * NAIVE_MARGIN);
    return ok & check("naive " + NAIVE_MARGIN + "x", slow, r -> naiveMarginRule(r, last), lines);
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

  /** A run's results, or the compile output when it did not compile. */
  private record Outcome(RunResults results, String compileError, long durationMs) {}

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
      return new Outcome(null, compiled.output(), elapsedMs(start));
    }
    var request = new RunRequest(work, out, target, variant, LIMITS, runFactor);
    var output = pkg.runner().run(request, processes);
    return new Outcome(RunProtocolParser.parse(output.resultsFile()), null, elapsedMs(start));
  }

  private static long elapsedMs(long startNs) {
    return (System.nanoTime() - startNs) / 1_000_000;
  }

  private static String starterRule(RunResults r, String first) {
    return count(r, first, TestStatus.FAIL) + count(r, first, TestStatus.TIMEOUT) > 0
        ? null
        : "the first phase must have a failing test";
  }

  private String referenceRule(RunResults r) {
    if (r.tests().stream().anyMatch(t -> t.status() != TestStatus.PASS)
        || r.suites().stream().anyMatch(s -> r.notRun(s.name()) > 0)) {
      return "every test must pass";
    }
    for (TestResult t : r.tests()) {
      if (t.limitMs() != null && t.ms() * HEADROOM > t.limitMs()) {
        return t.suite()
            + "."
            + t.name()
            + " took "
            + t.ms()
            + " ms; its "
            + t.limitMs()
            + " ms limit needs "
            + HEADROOM
            + "x headroom";
      }
    }
    return null;
  }

  private static String naiveRule(RunResults r, String last) {
    boolean othersPass =
        r.tests().stream()
            .filter(t -> !t.suite().equals(last))
            .allMatch(t -> t.status() == TestStatus.PASS);
    return othersPass && count(r, last, TestStatus.TIMEOUT) > 0
        ? null
        : "every phase but the last must pass and the last must have a TIMEOUT";
  }

  private static String naiveMarginRule(RunResults r, String last) {
    return count(r, last, TestStatus.TIMEOUT) > 0
        ? null
        : "the naive solution must still time out at " + NAIVE_MARGIN + "x the limits";
  }

  private static long count(RunResults r, String suite, TestStatus status) {
    return r.tests().stream().filter(t -> t.suite().equals(suite) && t.status() == status).count();
  }

  /** Prints one line per run, in the prototype's format; a rule returns null when it holds. */
  private static boolean check(
      String label, Outcome outcome, Function<RunResults, String> rule, List<String> lines) {
    String broken = outcome.compileError() != null ? null : rule.apply(outcome.results());
    boolean ok = outcome.compileError() == null && broken == null;
    StringBuilder line =
        new StringBuilder(
            String.format(
                Locale.ROOT,
                "  %-10s %-5s %6.1f s  ",
                label,
                ok ? "ok" : "FAIL",
                outcome.durationMs() / 1000.0));
    if (outcome.compileError() != null) {
      line.append("did not compile: ")
          .append(outcome.compileError().lines().skip(2).findFirst().orElse(""));
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
