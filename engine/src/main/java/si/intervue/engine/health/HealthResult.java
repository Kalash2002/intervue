package si.intervue.engine.health;

/**
 * Outcome of a check: {@code message} is what the user reads (a fix on failure), {@code measured}
 * the observed value for the copy-report button, such as "21.0.2" or "factor 1.30".
 */
public record HealthResult(Status status, String message, String measured) {

  public enum Status {
    PASS,
    WARN,
    FAIL
  }

  public static HealthResult pass(String message, String measured) {
    return new HealthResult(Status.PASS, message, measured);
  }

  public static HealthResult warn(String message, String measured) {
    return new HealthResult(Status.WARN, message, measured);
  }

  public static HealthResult fail(String message, String measured) {
    return new HealthResult(Status.FAIL, message, measured);
  }
}
