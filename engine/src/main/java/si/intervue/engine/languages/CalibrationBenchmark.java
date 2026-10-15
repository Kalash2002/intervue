package si.intervue.engine.languages;

import si.intervue.engine.runners.ProcessControl;

/**
 * A fixed workload timed on this machine; the engine divides the median of three runs by {@code
 * referenceMs()} to get the calibration factor (PLAN.md 4.4).
 */
public interface CalibrationBenchmark {

  long referenceMs();

  /** Runs the workload once with the language's own toolchain and returns its milliseconds. */
  long measureMs(ProcessControl processes);
}
