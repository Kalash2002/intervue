package si.intervue.engine.session;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.nio.file.Path;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import si.intervue.engine.sections.Score;
import si.intervue.engine.sections.SessionType;

/** Finishing: the final run, the score, and what counts as solved (PLAN.md 2.8). */
class SessionFinishTest {

  private static final long MINUTE = 60_000;

  @TempDir Path root;
  EngineFixture engine;
  Sessions sessions;

  @BeforeEach
  void setUp() {
    engine = new EngineFixture(root);
    sessions = engine.sessions;
  }

  @AfterEach
  void tearDown() {
    engine.close();
  }

  private String solvedSession(SessionType type) {
    String id = sessions.create("tiny", type).id();
    engine.clock.advance(2 * MINUTE);
    sessions.writeFile(id, "Calc.java", EngineFixture.FULL_CALC);
    sessions.run(id, "phase:1");
    engine.clock.advance(MINUTE);
    return id;
  }

  @Test
  void finishRunsEveryPhaseAndScoresTheInterview() {
    String id = solvedSession(SessionType.INTERVIEW);

    SessionView view = sessions.finish(id);

    assertThat(view.finished()).isTrue();
    Score score = view.result().score();
    assertThat(score.label()).isEqualTo("Interview");
    assertThat(score.solved()).isTrue();
    assertThat(view.result().finalResults()).allMatch(p -> p.green());
    assertThat(view.result().walkthrough()).contains("walkthrough");
    assertThat(view.result().followUps()).containsExactly("Why?");
    assertThat(view.brief()).doesNotContainNull();
    assertThat(view.events()).extracting(EventView::type).endsWith("finish");
  }

  @Test
  void aPhaseThatFirstPassesInTheFinalRunCounts() {
    String id = sessions.create("tiny", SessionType.INTERVIEW).id();
    sessions.writeFile(id, "Calc.java", EngineFixture.FULL_CALC);

    SessionView view = sessions.finish(id);

    assertThat(view.phasesPassed()).containsKeys(1, 2);
  }

  @Test
  void finishingTwiceChangesNothingAndFilesBecomeReadOnly() {
    String id = solvedSession(SessionType.INTERVIEW);
    SessionView first = sessions.finish(id);
    engine.clock.advance(30 * MINUTE);

    SessionView second = sessions.finish(id);

    assertThat(second.activeMs()).isEqualTo(first.activeMs());
    assertThat(second.events()).hasSameSizeAs(first.events());
    assertThatThrownBy(() -> sessions.writeFile(id, "Calc.java", "x"))
        .isInstanceOf(SessionException.class);
  }

  @Test
  void anAssistedInterviewThatPassesStillCountsAsSolved() {
    String id = solvedSession(SessionType.INTERVIEW);
    SessionRunsTest.insertActiveEdit(engine, id);

    SessionView view = sessions.finish(id);

    // ADR 0006: assisted is recorded and shown, but never changes the label or solved.
    assertThat(view.assisted()).isTrue();
    assertThat(view.result().score().label()).isEqualTo("Interview");
    assertThat(sessions.history())
        .filteredOn(s -> s.id().equals(id))
        .allMatch(SessionSummary::countsAsSolved);
  }

  @Test
  void practiceIsScoredAsPracticeAndNeverCountsAsSolved() {
    String id = solvedSession(SessionType.PRACTICE);

    SessionView view = sessions.finish(id);

    assertThat(view.result().score().label()).isEqualTo("Practice");
    assertThat(view.result().score().solved()).isTrue();
    assertThat(sessions.history())
        .filteredOn(s -> s.id().equals(id))
        .allMatch(s -> !s.countsAsSolved());
  }

  @Test
  void aSolvedInterviewCountsAsSolved() {
    String id = solvedSession(SessionType.INTERVIEW);
    sessions.finish(id);

    assertThat(sessions.history())
        .filteredOn(s -> s.id().equals(id))
        .allMatch(SessionSummary::countsAsSolved);
  }

  @Test
  void theTimeScoreUsesActiveTimeSoAClosedAppDoesNotCost() {
    String id = solvedSession(SessionType.INTERVIEW);
    sessions.onShutdown();
    engine.clock.advance(90 * MINUTE);
    sessions.onStartup();

    Score score = sessions.finish(id).result().score();

    Score.ScorePart time =
        score.parts().stream().filter(p -> p.name().equals("Time")).findFirst().orElseThrow();
    assertThat(time.points()).isEqualTo(10.0);
    assertThat(sessions.view(id).activeMs()).isEqualTo(3 * MINUTE);
  }
}
