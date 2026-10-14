package si.intervue.engine.health;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Clock;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import si.intervue.engine.health.HealthResult.Status;
import si.intervue.engine.store.Database;

/**
 * Runs the health checks (PLAN.md 6.4): all of them for the Doctor screen, the fast subset before a
 * session. Results go to {@code health_results}; a check that throws counts as a failure.
 */
public final class Doctor {

  private final Database db;
  private final List<HealthCheck> checks;
  private final Clock clock;

  public Doctor(Database db, List<HealthCheck> checks, Clock clock) {
    this.db = db;
    this.checks = List.copyOf(checks);
    this.clock = clock;
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
              clock.millis());
      store(stored);
      results.add(stored);
      ready &= !(check.blocking() && result.status() == Status.FAIL);
    }
    return new DoctorReport(ready, results);
  }

  /** The latest stored result of every check that has one, in declaration order. */
  public List<CheckResult> lastResults() {
    Map<String, CheckResult> stored = new HashMap<>();
    String sql = "SELECT check_id, status, message, measured, ran_at FROM health_results";
    try (PreparedStatement ps = db.connection().prepareStatement(sql);
        ResultSet rs = ps.executeQuery()) {
      while (rs.next()) {
        stored.put(
            rs.getString(1),
            new CheckResult(
                rs.getString(1),
                null,
                false,
                Status.valueOf(rs.getString(2)),
                rs.getString(3),
                rs.getString(4),
                rs.getLong(5)));
      }
    } catch (SQLException e) {
      throw new IllegalStateException("Cannot read health results", e);
    }
    List<CheckResult> results = new ArrayList<>();
    for (HealthCheck check : checks) {
      CheckResult r = stored.get(check.id());
      if (r != null) {
        results.add(
            new CheckResult(
                r.id(),
                check.title(),
                check.blocking(),
                r.status(),
                r.message(),
                r.measured(),
                r.ranAt()));
      }
    }
    return results;
  }

  private void store(CheckResult r) {
    String sql =
        "INSERT INTO health_results(check_id, status, message, measured, ran_at)"
            + " VALUES (?, ?, ?, ?, ?) ON CONFLICT(check_id) DO UPDATE SET status = excluded.status,"
            + " message = excluded.message, measured = excluded.measured, ran_at = excluded.ran_at";
    try (PreparedStatement ps = db.connection().prepareStatement(sql)) {
      ps.setString(1, r.id());
      ps.setString(2, r.status().name());
      ps.setString(3, r.message());
      ps.setString(4, r.measured());
      ps.setLong(5, r.ranAt());
      ps.executeUpdate();
    } catch (SQLException e) {
      throw new IllegalStateException("Cannot store health result " + r.id(), e);
    }
  }
}
