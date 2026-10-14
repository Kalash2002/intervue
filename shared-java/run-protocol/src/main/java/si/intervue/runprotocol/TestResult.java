package si.intervue.runprotocol;

/**
 * One reported test. {@code detail}, {@code at} and {@code limitMs} are null when the line did not
 * carry them.
 */
public record TestResult(
    String suite,
    String name,
    TestStatus status,
    long ms,
    String detail,
    String at,
    Long limitMs) {}
