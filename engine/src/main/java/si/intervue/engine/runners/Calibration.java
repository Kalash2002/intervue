package si.intervue.engine.runners;

import java.util.ArrayList;
import java.util.List;
import si.intervue.engine.languages.CalibrationBenchmark;

/** The calibration factor of PLAN.md 4.4: median of three runs over the reference, clamped. */
public final class Calibration {

  public static final double MIN_FACTOR = 1.0;
  public static final double MAX_FACTOR = 4.0;
  private static final int RUNS = 3;

  private Calibration() {}

  public static double factor(CalibrationBenchmark benchmark, ProcessControl processes) {
    List<Long> runs = new ArrayList<>();
    for (int i = 0; i < RUNS; i++) {
      runs.add(benchmark.measureMs(processes));
    }
    runs.sort(null);
    return clamp((double) runs.get(RUNS / 2) / benchmark.referenceMs());
  }

  /** A machine faster than the reference keeps the reference limits; slower ones get more time. */
  static double clamp(double raw) {
    return Math.max(MIN_FACTOR, Math.min(MAX_FACTOR, raw));
  }
}
