package si.intervue.runprotocol;

/** Outcome of one test; {@code ERROR} means the harness failed, not the candidate's code. */
public enum TestStatus {
  PASS,
  FAIL,
  TIMEOUT,
  ERROR
}
