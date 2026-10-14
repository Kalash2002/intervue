package si.intervue.languages.java;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import si.intervue.engine.health.HealthCheck;
import si.intervue.engine.health.HealthResult;
import si.intervue.engine.languages.CompileResult;
import si.intervue.engine.languages.RunLimits;
import si.intervue.engine.languages.RunOutput;
import si.intervue.engine.languages.RunRequest;
import si.intervue.engine.runners.ProcessControl;
import si.intervue.problemformat.Variant;

/**
 * The Java package's Doctor checks (PLAN.md 6.4): the bundled runtime answers with the engine's own
 * version, a sample compiles and runs through the real runner, and UTF-8 text in a CRLF source
 * comes back byte-exact. Calibration is measured by the engine through {@link JavaBenchmark}.
 */
final class JavaHealthChecks {

  private static final RunLimits LIMITS = new RunLimits(10_000, 256, 64_000);

  private static final String SAMPLE =
      "public class Sample {\n  public static void main(String[] args) {\n"
          + "    System.out.println(java.util.stream.IntStream.rangeClosed(1, 100).sum());\n"
          + "  }\n}\n";

  // CRLF line endings on purpose: the text block must still come out with plain newlines.
  private static final String ENCODING =
      "public class Sample {\r\n  public static void main(String[] args) {\r\n"
          + "    System.out.print(\"\"\"\r\n        héllo ✓ ₹ 日本\r\n        line two\r\n"
          + "        \"\"\");\r\n  }\r\n}\r\n";

  private final JavaRunner runner;
  private final ProcessControl processes = new ProcessControl();

  JavaHealthChecks(JavaRunner runner) {
    this.runner = runner;
  }

  List<HealthCheck> all() {
    return List.of(
        new HealthCheck("java.runtime", "Bundled Java runtime", true, true, this::runtime),
        new HealthCheck("java.sample", "Java sample compiles and runs", true, false, this::sample),
        new HealthCheck("java.encoding", "Text encoding", false, false, this::encoding));
  }

  // The runtime that runs sessions is the one the engine runs on, so their versions must match.
  // ponytail: no hash of lib/modules yet; add it when the jlink step records a reference hash.
  private HealthResult runtime() {
    String expected = System.getProperty("java.version");
    ProcessControl.Result ran =
        processes.run(List.of(JavaRunner.javaExecutable(), "-version"), Path.of("."), 10_000, 8000);
    if (ran.exitCode() == 0 && ran.output().contains("\"" + expected + "\"")) {
      return HealthResult.pass("Java " + expected, expected);
    }
    return HealthResult.fail(
        "The bundled Java runtime is damaged (likely antivirus). Reinstall Intervue.",
        firstLine(ran.output()));
  }

  private HealthResult sample() {
    long started = System.nanoTime();
    String output = compileAndRun(SAMPLE);
    long ms = (System.nanoTime() - started) / 1_000_000;
    return output.equals("5050\n")
        ? HealthResult.pass("The sample compiled and ran", ms + " ms")
        : HealthResult.fail("The sample program failed: " + firstLine(output), ms + " ms");
  }

  private HealthResult encoding() {
    String output = compileAndRun(ENCODING);
    return output.equals("héllo ✓ ₹ 日本\nline two\n")
        ? HealthResult.pass("UTF-8 text and CRLF sources are handled", "UTF-8")
        : HealthResult.warn("Text output is garbled on this machine", firstLine(output));
  }

  /** The program's output, or the compiler's or runner's complaint when it did not finish. */
  private String compileAndRun(String source) {
    Path dir = null;
    try {
      dir = Files.createTempDirectory("intervue-doctor");
      Path work = Files.createDirectory(dir.resolve("work"));
      Files.writeString(work.resolve("Sample.java"), source, StandardCharsets.UTF_8);
      Path out = dir.resolve("out");
      CompileResult compiled = runner.compile(work, out);
      if (!compiled.ok()) {
        return compiled.output();
      }
      Variant variant = new Variant("java", null, "Sample", List.of(), Map.of(), null, null, null);
      RunOutput ran =
          runner.run(new RunRequest(work, out, "main", variant, LIMITS, 1.0), processes);
      return ran.exitCode() == 0 ? ran.output() : "exit " + ran.exitCode() + ": " + ran.output();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    } finally {
      deleteQuietly(dir);
    }
  }

  private static String firstLine(String text) {
    return text.strip().lines().findFirst().orElse("(no output)");
  }

  private static void deleteQuietly(Path dir) {
    if (dir == null) {
      return;
    }
    try (Stream<Path> walk = Files.walk(dir)) {
      for (Path p : walk.sorted(Comparator.reverseOrder()).toList()) {
        Files.deleteIfExists(p);
      }
    } catch (IOException e) {
      // A leftover temp folder is harmless; the OS cleans its temp directory.
    }
  }
}
