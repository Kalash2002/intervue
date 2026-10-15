package si.intervue.runprotocol;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

import java.net.URISyntaxException;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class RunProtocolParserTest {

  private static RunResults parse(String name) {
    return RunProtocolParser.parse(golden(name));
  }

  private static Path golden(String name) {
    try {
      return Path.of(RunProtocolParserTest.class.getResource("/runs/" + name).toURI());
    } catch (URISyntaxException e) {
      throw new IllegalStateException(e);
    }
  }

  @Test
  void readsEveryLineTypeAndStatus() {
    RunResults results = parse("mixed.jsonl");

    assertThat(results.runner()).isEqualTo("java-kit/2.0");
    assertThat(results.language()).isEqualTo("java");
    assertThat(results.suites())
        .containsExactly(new Suite("Phase2Tests", 3), new Suite("Phase3Tests", 1));
    assertThat(results.tests())
        .extracting(TestResult::name, TestResult::status)
        .containsExactly(
            tuple("singleHold", TestStatus.PASS),
            tuple("sampleFile", TestStatus.FAIL),
            tuple("harness", TestStatus.ERROR),
            tuple("oneBusyAccount", TestStatus.TIMEOUT));
    TestResult failed = results.tests().get(1);
    assertThat(failed.detail()).isEqualTo("expected <255049> but was <255048>");
    assertThat(failed.at()).isEqualTo("Phase2Tests.sampleFile(Phase2Tests.java:118)");
    assertThat(results.tests().get(3).limitMs()).isEqualTo(7800L);
    assertThat(results.summary()).contains(new Summary(1, 1, 1, false, 9120));
  }

  @Test
  void ignoresUnknownLineTypesAndFields() {
    RunResults results = parse("additive.jsonl");

    assertThat(results.tests()).hasSize(1);
    assertThat(results.tests().get(0).status()).isEqualTo(TestStatus.PASS);
    assertThat(results.summary()).isPresent();
  }

  @Test
  void refusesAProtocolVersionItDoesNotKnow() {
    assertThatThrownBy(() -> parse("future.jsonl"))
        .isInstanceOf(RunProtocolException.class)
        .hasMessageContaining("protocol 2");
  }

  @Test
  void refusesResultsThatDoNotStartWithAStartLine() {
    assertThatThrownBy(() -> parse("nostart.jsonl"))
        .isInstanceOf(RunProtocolException.class)
        .hasMessageContaining("start");
  }

  @Test
  void keepsReportedTestsWhenTheRunnerWasKilledMidLine() {
    RunResults results = parse("killed.jsonl");

    assertThat(results.tests()).extracting(TestResult::name).containsExactly("a");
    assertThat(results.summary()).isEmpty();
    assertThat(results.notRun("Phase1Tests")).isEqualTo(2);
  }

  @Test
  void refusesACorruptLineThatIsNotTheLast() {
    assertThatThrownBy(() -> parse("corrupt.jsonl"))
        .isInstanceOf(RunProtocolException.class)
        .hasMessageContaining("line 2");
  }

  @Test
  void treatsAnUnknownStatusAsAHarnessError() {
    assertThat(parse("badstatus.jsonl").tests().get(0).status()).isEqualTo(TestStatus.ERROR);
  }

  @Test
  void refusesAMissingFile() {
    assertThatThrownBy(() -> RunProtocolParser.parse(Path.of("does-not-exist.jsonl")))
        .isInstanceOf(RunProtocolException.class);
  }
}
