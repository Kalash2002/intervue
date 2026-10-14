package si.intervue.engine.sections;

/**
 * How one phase's tests ended in one run, with the phase's weight from {@code problem.json}. {@code
 * failed} includes harness errors; {@code notRun} counts tests the suite announced but never
 * reported (the run was stopped).
 */
public record PhaseResult(
    int phaseId,
    String title,
    int weight,
    boolean allOrNothing,
    int passed,
    int failed,
    int timeouts,
    int notRun) {

  /** Every test of the phase ran and passed. */
  public boolean green() {
    return passed > 0 && failed == 0 && timeouts == 0 && notRun == 0;
  }

  public int total() {
    return passed + failed + timeouts + notRun;
  }
}
