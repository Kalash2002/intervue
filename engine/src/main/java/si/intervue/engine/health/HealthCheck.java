package si.intervue.engine.health;

import java.util.function.Supplier;

/**
 * One Doctor check (PLAN.md 6.4), declared by the engine or by a language or section package.
 * {@code id} is stable, such as "java.sample"; results are stored under it. A failing {@code
 * blocking} check means a session could not run or be scored correctly. {@code fast} checks also
 * run before every session and must finish in well under a second.
 */
public record HealthCheck(
    String id, String title, boolean blocking, boolean fast, Supplier<HealthResult> probe) {

  public HealthResult run() {
    return probe.get();
  }
}
