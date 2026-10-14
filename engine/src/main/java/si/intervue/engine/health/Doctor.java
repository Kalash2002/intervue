package si.intervue.engine.health;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import si.intervue.engine.health.HealthResult.Status;
import si.intervue.engine.store.Database;

/**
 * Runs the health checks (PLAN.md 6.4): all of them for the Doctor screen, the fast subset before a
 * session. Results go to {@code health_results}; a check that throws counts as a failure.
 */
public final class Doctor {

  private final Database db;
  private final List<HealthCheck> checks;

  public Doctor(Database db, List<HealthCheck> checks) {
    this.db = db;
    this.checks = List.copyOf(checks);
  }

  public DoctorReport runAll() {
    return run(checks);
  }

  public DoctorReport runFast() {
    return run(checks.stream().filter(HealthCheck::fast).toList());
  }

  private DoctorReport run(List<HealthCheck> selected) {
    List<CheckResult> results = new ArrayList<>();
    boolean ready = true;
    for (HealthCheck check : selected) {
      HealthResult result;
      try {
        result = check.run();
      } catch (RuntimeException e) {
        result = HealthResult.fail("The check itself failed: " + e.getMessage(), null);
      }
      CheckResult stored =
          new CheckResult(
              check.id(),
              check.title(),
              check.blocking(),
              result.status(),
              result.message(),
              result.measured(),
              System.currentTimeMillis());
      store(stored);
      results.add(stored);
      ready &= !(check.blocking() && result.status() == Status.FAIL);
    }
    return new DoctorReport(ready, results);
  }

  /** The latest stored result of every check, in declaration order. */
  public List<CheckResult> lastResults() {
    List<CheckResult> results = new ArrayList<>();
    String sql = "SELECT status, measured, ran_at FROM health_results WHERE check_id = ?";
    for (HealthCheck check : checks) {
      try (PreparedStatement ps = db.connection().prepareStatement(sql)) {
        ps.setString(1, check.id());
        try (ResultSet rs = ps.executeQuery()) {
          if (rs.next()) {
            String stored = rs.getString(1);
            int bar = stored.indexOf('|');
            String status = bar < 0 ? stored : stored.substring(0, bar);
            String message = bar < 0 ? "" : stored.substring(bar + 1);
            results.add(
                new CheckResult(
                    check.id(),
                    check.title(),
                    check.blocking(),
                    Status.valueOf(status),
                    message,
                    rs.getString(2),
                    rs.getLong(3)));
          }
        }
      } catch (SQLException e) {
        throw new IllegalStateException("Cannot read health results", e);
      }
    }
    return results;
  }

  // The v1 table has no message column, so the message rides with the status as "STATUS|message".
  private void store(CheckResult r) {
    String sql =
        "INSERT INTO health_results(check_id, status, measured, ran_at) VALUES (?, ?, ?, ?)"
            + " ON CONFLICT(check_id) DO UPDATE SET status = excluded.status, measured = excluded.measured,"
            + " ran_at = excluded.ran_at";
    try (PreparedStatement ps = db.connection().prepareStatement(sql)) {
      ps.setString(1, r.id());
      ps.setString(2, r.status().name() + "|" + (r.message() == null ? "" : r.message()));
      ps.setString(3, r.measured());
      ps.setLong(4, r.ranAt());
      ps.executeUpdate();
    } catch (SQLException e) {
      throw new IllegalStateException("Cannot store health result " + r.id(), e);
    }
  }
}
