package si.intervue.languages.java;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import si.intervue.engine.languages.CalibrationBenchmark;
import si.intervue.engine.runners.ProcessControl;

/** Runs {@code calibration/Bench.java} with the bundled JDK in source-launch mode. */
final class JavaBenchmark implements CalibrationBenchmark {

  private static final long WALL_MS = 120_000;
  private final long referenceMs;

  JavaBenchmark(long referenceMs) {
    this.referenceMs = referenceMs;
  }

  @Override
  public long referenceMs() {
    return referenceMs;
  }

  @Override
  public long measureMs(ProcessControl processes) {
    Path dir = null;
    try {
      dir = Files.createTempDirectory("intervue-bench");
      Path bench = dir.resolve("Bench.java");
      try (InputStream in = JavaBenchmark.class.getResourceAsStream("/calibration/Bench.java")) {
        Files.copy(in, bench);
      }
      List<String> command = List.of(JavaRunner.javaExecutable(), "-Xmx512m", bench.toString());
      ProcessControl.Result result = processes.run(command, dir, WALL_MS, 10_000);
      if (result.timedOut() || result.exitCode() != 0) {
        throw new IllegalStateException("The calibration benchmark failed: " + result.output());
      }
      return lastNumber(result.output());
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    } finally {
      deleteQuietly(dir);
    }
  }

  // stderr is merged into stdout, so the time is the last line that is a plain number.
  private static long lastNumber(String output) {
    String[] lines = output.strip().split("\\R");
    for (int i = lines.length - 1; i >= 0; i--) {
      if (lines[i].strip().matches("\\d+")) {
        return Long.parseLong(lines[i].strip());
      }
    }
    throw new IllegalStateException("The calibration benchmark printed no time: " + output);
  }

  private static void deleteQuietly(Path dir) {
    if (dir == null) {
      return;
    }
    try {
      Files.deleteIfExists(dir.resolve("Bench.java"));
      Files.deleteIfExists(dir);
    } catch (IOException e) {
      // A leftover temp folder is harmless; the OS cleans its temp directory.
    }
  }
}
