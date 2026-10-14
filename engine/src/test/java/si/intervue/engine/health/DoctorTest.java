package si.intervue.engine.health;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import si.intervue.engine.health.HealthResult.Status;
import si.intervue.engine.store.Database;

class DoctorTest {

  @TempDir Path root;
  Database db;

  @BeforeEach
  void open() {
    db = Database.open(root.resolve("intervue.db"));
  }

  @AfterEach
  void close() {
    db.close();
  }

  private static HealthCheck check(String id, boolean blocking, boolean fast, HealthResult result) {
    return new HealthCheck(
        id,
        "Check " + id,
        blocking,
        fast,
        () -> {
          if (result == null) {
            throw new IllegalStateException("boom");
          }
          return result;
        });
  }

  @Test
  void runsEveryCheckStoresTheResultsAndIsReadyWhenNothingBlockingFails() {
    Doctor doctor =
        new Doctor(
            db,
            List.of(
                check("a", true, true, HealthResult.pass("fine", "1")),
                check("b", false, false, HealthResult.warn("slow machine", "factor 2.6"))));

    DoctorReport report = doctor.runAll();

    assertThat(report.ready()).isTrue();
    assertThat(report.results()).extracting(CheckResult::id).containsExactly("a", "b");
    assertThat(doctor.lastResults())
        .extracting(CheckResult::status)
        .containsExactly(Status.PASS, Status.WARN);
  }

  @Test
  void aFailingBlockingCheckMeansNotReady() {
    Doctor doctor =
        new Doctor(db, List.of(check("a", true, true, HealthResult.fail("broken", null))));

    assertThat(doctor.runAll().ready()).isFalse();
  }

  @Test
  void theFastSubsetSkipsSlowChecks() {
    Doctor doctor =
        new Doctor(
            db,
            List.of(
                check("fast", true, true, HealthResult.pass("ok", null)),
                check("slow", true, false, HealthResult.fail("would fail", null))));

    DoctorReport report = doctor.runFast();

    assertThat(report.results()).extracting(CheckResult::id).containsExactly("fast");
    assertThat(report.ready()).isTrue();
  }

  @Test
  void aCheckThatThrowsIsReportedAsAFailureNotACrash() {
    Doctor doctor = new Doctor(db, List.of(check("x", true, true, null)));

    DoctorReport report = doctor.runAll();

    assertThat(report.results().get(0).status()).isEqualTo(Status.FAIL);
    assertThat(report.results().get(0).message()).contains("boom");
  }
}
