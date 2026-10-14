package si.intervue.engine.health;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import si.intervue.engine.health.HealthResult.Status;
import si.intervue.engine.store.Database;

class DoctorTest {

  private static final Clock CLOCK = Clock.fixed(Instant.ofEpochMilli(42), ZoneOffset.UTC);

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
                check("b", false, false, HealthResult.warn("slow machine", "factor 2.6"))),
            CLOCK);

    DoctorReport report = doctor.runAll();

    assertThat(report.ready()).isTrue();
    assertThat(report.results()).extracting(CheckResult::id).containsExactly("a", "b");
    assertThat(doctor.lastResults())
        .extracting(CheckResult::status, CheckResult::message, CheckResult::ranAt)
        .containsExactly(tuple(Status.PASS, "fine", 42L), tuple(Status.WARN, "slow machine", 42L));
  }

  @Test
  void aFailingBlockingCheckMeansNotReady() {
    Doctor doctor =
        new Doctor(db, List.of(check("a", true, true, HealthResult.fail("broken", null))), CLOCK);

    assertThat(doctor.runAll().ready()).isFalse();
  }

  @Test
  void theFastSubsetSkipsSlowChecks() {
    Doctor doctor =
        new Doctor(
            db,
            List.of(
                check("fast", true, true, HealthResult.pass("ok", null)),
                check("slow", true, false, HealthResult.fail("would fail", null))),
            CLOCK);

    DoctorReport report = doctor.runFast();

    assertThat(report.results()).extracting(CheckResult::id).containsExactly("fast");
    assertThat(report.ready()).isTrue();
  }

  @Test
  void aCheckThatThrowsIsReportedAsAFailureNotACrash() {
    Doctor doctor = new Doctor(db, List.of(check("x", true, true, null)), CLOCK);

    DoctorReport report = doctor.runAll();

    assertThat(report.results().get(0).status()).isEqualTo(Status.FAIL);
    assertThat(report.results().get(0).message()).contains("boom");
  }
}
