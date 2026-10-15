package si.intervue.content;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import si.intervue.runprotocol.RunResults;
import si.intervue.runprotocol.Suite;
import si.intervue.runprotocol.TestResult;
import si.intervue.runprotocol.TestStatus;

class ReferenceRuleTest {

  private static RunResults run(TestStatus status, long loadMs, long searchMs) {
    return new RunResults(
        "java-kit/2.0",
        "java",
        List.of(new Suite("Phase3Tests", 2)),
        List.of(
            new TestResult("Phase3Tests", "load", status, loadMs, null, null, 10_000L),
            new TestResult(
                "Phase3Tests", "search", TestStatus.PASS, searchMs, null, null, 10_000L)),
        Optional.empty());
  }

  @Test
  void holdsWhenEveryTestHasFiveTimesHeadroom() {
    assertThat(ProblemVerifier.referenceRule(List.of(run(TestStatus.PASS, 1_000, 2_000)))).isNull();
  }

  @Test
  void judgesHeadroomOnTheFastestRunOfEachTest() {
    // Noise only ever adds time, so the fastest run is the best estimate of a test's real cost.
    var noisy = run(TestStatus.PASS, 6_000, 1_000);
    var calm = run(TestStatus.PASS, 1_500, 3_000);

    assertThat(ProblemVerifier.referenceRule(List.of(noisy, calm))).isNull();
  }

  @Test
  void failsWhenEvenTheFastestRunLacksHeadroom() {
    var rule =
        ProblemVerifier.referenceRule(
            List.of(run(TestStatus.PASS, 2_500, 1_000), run(TestStatus.PASS, 2_200, 1_000)));

    assertThat(rule).contains("load").contains("2200").contains("5x");
  }

  @Test
  void failsWhenTheFirstRunHasAFailingTest() {
    assertThat(ProblemVerifier.referenceRule(List.of(run(TestStatus.FAIL, 1, 1))))
        .contains("every test must pass");
  }
}
