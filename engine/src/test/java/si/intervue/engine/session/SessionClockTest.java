package si.intervue.engine.session;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import si.intervue.engine.sections.SessionType;

/** The clock pauses whenever the app is closed (PLAN.md 2.8, "Clock and pauses"). */
class SessionClockTest {

  private static final long MINUTE = 60_000;

  @TempDir Path root;
  EngineFixture engine;

  @BeforeEach
  void setUp() {
    engine = new EngineFixture(root);
  }

  @AfterEach
  void tearDown() {
    engine.close();
  }

  @Test
  void activeTimeRunsOnTheWallClockWhileTheAppIsOpen() {
    String id = engine.sessions.create("tiny", SessionType.INTERVIEW).id();
    engine.clock.advance(5 * MINUTE);

    assertThat(engine.sessions.view(id).activeMs()).isEqualTo(5 * MINUTE);
  }

  @Test
  void aCrashPausesTheClockFromTheLastHeartbeatUntilTheNextStart() {
    String id = engine.sessions.create("tiny", SessionType.INTERVIEW).id();
    engine.clock.advance(3 * MINUTE);
    engine.sessions.heartbeat();
    engine.clock.advance(10_000);
    // The app dies here without a clean shutdown and stays closed for 40 minutes.
    engine.clock.advance(40 * MINUTE);

    engine.sessions.onStartup();
    engine.clock.advance(2 * MINUTE);

    assertThat(engine.sessions.view(id).activeMs()).isEqualTo(5 * MINUTE);
    assertThat(engine.sessions.view(id).events())
        .extracting(EventView::type)
        .containsSubsequence("pause", "resume");
  }

  @Test
  void aCleanShutdownPausesAtTheMomentItHappens() {
    String id = engine.sessions.create("tiny", SessionType.PRACTICE).id();
    engine.clock.advance(4 * MINUTE);
    engine.sessions.onShutdown();
    engine.clock.advance(60 * MINUTE);

    engine.sessions.onStartup();
    engine.clock.advance(MINUTE);

    assertThat(engine.sessions.view(id).activeMs()).isEqualTo(5 * MINUTE);
  }

  @Test
  void startingTwiceWithoutAHeartbeatDoesNotPauseAgain() {
    String id = engine.sessions.create("tiny", SessionType.INTERVIEW).id();
    engine.sessions.onShutdown();
    engine.sessions.onStartup();
    engine.sessions.onStartup();

    assertThat(engine.sessions.view(id).events())
        .extracting(EventView::type)
        .filteredOn(t -> t.equals("pause"))
        .hasSize(1);
  }

  @Test
  void eventTimesAreActiveTimeSoPausesDoNotCountAsGaps() {
    String id = engine.sessions.create("tiny", SessionType.INTERVIEW).id();
    engine.clock.advance(2 * MINUTE);
    engine.sessions.onShutdown();
    engine.clock.advance(30 * MINUTE);
    engine.sessions.onStartup();
    engine.clock.advance(MINUTE);
    engine.sessions.saveNotes(id, "x");
    engine.sessions.skip(id);

    EventView skip =
        engine.sessions.view(id).events().stream()
            .filter(e -> e.type().equals("skip"))
            .findFirst()
            .orElseThrow();
    assertThat(skip.tMs()).isEqualTo(3 * MINUTE);
  }
}
