package si.intervue.runprotocol;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

/**
 * The only reader of {@code results.jsonl} (docs/contracts/run-protocol.md). Unknown line types and
 * fields are ignored so the protocol can grow additively; anything else wrong refuses the run.
 */
public final class RunProtocolParser {

  /** The protocol version this parser reads. */
  public static final int PROTOCOL = 1;

  private static final ObjectMapper JSON =
      JsonMapper.builder().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).build();

  /** Every field any line type may carry; each line uses a few of them. */
  private record Line(
      String t,
      Integer protocol,
      String runner,
      String language,
      String name,
      Integer tests,
      String suite,
      String status,
      Long ms,
      String detail,
      String at,
      Long limitMs,
      Integer passed,
      Integer failed,
      Integer timeouts,
      Boolean compileError,
      Long wallMs) {}

  private RunProtocolParser() {}

  public static RunResults parse(Path resultsFile) {
    try {
      return parse(Files.readAllLines(resultsFile, StandardCharsets.UTF_8));
    } catch (NoSuchFileException e) {
      throw new RunProtocolException("No results file: " + resultsFile, e);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public static RunResults parse(List<String> rawLines) {
    List<Line> lines = readLines(rawLines);
    Line start = lines.isEmpty() ? null : lines.get(0);
    if (start == null || !"start".equals(start.t()) || start.protocol() == null) {
      throw new RunProtocolException("Results do not begin with a start line");
    }
    if (start.protocol() != PROTOCOL) {
      throw new RunProtocolException("Runner protocol " + start.protocol() + " is not supported");
    }
    List<Suite> suites = new ArrayList<>();
    List<TestResult> tests = new ArrayList<>();
    Summary summary = null;
    for (Line line : lines.subList(1, lines.size())) {
      switch (line.t() == null ? "" : line.t()) {
        case "suite" -> suites.add(new Suite(line.name(), orZero(line.tests())));
        case "test" -> tests.add(toTest(line));
        case "summary" -> summary = toSummary(line);
        default -> {
          // Unknown line types are additive protocol growth; skip them.
        }
      }
    }
    return new RunResults(
        start.runner(), start.language(), suites, tests, Optional.ofNullable(summary));
  }

  /** A broken last line means the runner was killed mid-write; a broken earlier line is corrupt. */
  private static List<Line> readLines(List<String> rawLines) {
    List<Line> lines = new ArrayList<>();
    int last = lastNonBlank(rawLines);
    for (int i = 0; i <= last; i++) {
      String raw = rawLines.get(i);
      if (raw.isBlank()) {
        continue;
      }
      try {
        lines.add(JSON.readValue(raw, Line.class));
      } catch (JacksonException e) {
        if (i == last) {
          break;
        }
        throw new RunProtocolException("Corrupt results at line " + (i + 1), e);
      }
    }
    return lines;
  }

  private static int lastNonBlank(List<String> rawLines) {
    for (int i = rawLines.size() - 1; i >= 0; i--) {
      if (!rawLines.get(i).isBlank()) {
        return i;
      }
    }
    return -1;
  }

  private static TestResult toTest(Line line) {
    return new TestResult(
        line.suite(),
        line.name(),
        toStatus(line.status()),
        line.ms() == null ? 0 : line.ms(),
        line.detail(),
        line.at(),
        line.limitMs());
  }

  // A status this parser does not know cannot be graded as pass or fail; it is a harness error.
  private static TestStatus toStatus(String status) {
    if (status == null) {
      return TestStatus.ERROR;
    }
    try {
      return TestStatus.valueOf(status.toUpperCase(Locale.ROOT));
    } catch (IllegalArgumentException e) {
      return TestStatus.ERROR;
    }
  }

  private static Summary toSummary(Line line) {
    return new Summary(
        orZero(line.passed()),
        orZero(line.failed()),
        orZero(line.timeouts()),
        Boolean.TRUE.equals(line.compileError()),
        line.wallMs() == null ? 0 : line.wallMs());
  }

  private static int orZero(Integer value) {
    return value == null ? 0 : value;
  }
}
