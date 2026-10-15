package si.intervue.engine.runners;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayDeque;
import java.util.List;
import org.junit.jupiter.api.Test;
import si.intervue.engine.languages.CalibrationBenchmark;

class CalibrationTest {

  /** Replays fixed timings so the factor arithmetic is tested without running a JVM. */
  private static CalibrationBenchmark timings(long referenceMs, Long... runs) {
    ArrayDeque<Long> queue = new ArrayDeque<>(List.of(runs));
    return new CalibrationBenchmark() {
      @Override
      public long referenceMs() {
        return referenceMs;
      }

      @Override
      public long measureMs(ProcessControl processes) {
        return queue.removeFirst();
      }
    };
  }

  @Test
  void usesTheMedianOfThreeRuns() {
    assertThat(Calibration.factor(timings(1000, 2600L, 9000L, 2400L), null)).isEqualTo(2.6);
  }

  @Test
  void aFasterMachineKeepsTheReferenceLimits() {
    assertThat(Calibration.factor(timings(1000, 500L, 400L, 600L), null)).isEqualTo(1.0);
  }

  @Test
  void aVerySlowMachineIsCappedAtFour() {
    assertThat(Calibration.factor(timings(1000, 9000L, 9000L, 9000L), null)).isEqualTo(4.0);
  }
}
