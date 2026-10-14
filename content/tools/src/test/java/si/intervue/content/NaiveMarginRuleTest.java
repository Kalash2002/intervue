package si.intervue.content;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import si.intervue.runprotocol.RunResults;
import si.intervue.runprotocol.Suite;
import si.intervue.runprotocol.TestResult;
import si.intervue.runprotocol.TestStatus;

class NaiveMarginRuleTest {

  private static RunResults results(TestStatus first, TestStatus second) {
    return new RunResults(
        "java-kit/2.0",
        "java",
        List.of(new Suite("Phase3Tests", 2)),
        List.of(
            new TestResult("Phase3Tests", "load", first, 1, null, null, 1000L),
            new TestResult("Phase3Tests", "search", second, 1, null, null, 1000L)),
        Optional.empty());
  }

  @Test
  void holdsWhenEveryTimeoutStaysATimeoutAtTheMargin() {
    var atLimit = results(TestStatus.TIMEOUT, TestStatus.TIMEOUT);
    assertThat(ProblemVerifier.naiveMarginRule(atLimit, atLimit, "Phase3Tests")).isNull();
  }

  @Test
  void failsWhenOneTimeoutPassesAtTheMarginEvenIfAnotherStillTimesOut() {
    var atLimit = results(TestStatus.TIMEOUT, TestStatus.TIMEOUT);
    var atMargin = results(TestStatus.PASS, TestStatus.TIMEOUT);

    assertThat(ProblemVerifier.naiveMarginRule(atLimit, atMargin, "Phase3Tests")).contains("load");
  }

  @Test
  void ignoresTestsTheNaiveSolutionAlreadyPassedAtTheLimit() {
    var atLimit = results(TestStatus.PASS, TestStatus.TIMEOUT);
    var atMargin = results(TestStatus.PASS, TestStatus.TIMEOUT);

    assertThat(ProblemVerifier.naiveMarginRule(atLimit, atMargin, "Phase3Tests")).isNull();
  }
}
