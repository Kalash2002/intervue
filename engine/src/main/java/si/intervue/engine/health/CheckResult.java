package si.intervue.engine.health;

import si.intervue.engine.health.HealthResult.Status;

/** A check's result with its identity, as the Doctor screen and the store hold it. */
public record CheckResult(
    String id,
    String title,
    boolean blocking,
    Status status,
    String message,
    String measured,
    long ranAt) {}
