package si.intervue.engine.session;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.nio.file.Path;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import si.intervue.engine.sections.SessionType;

/** Hints: three authored levels per phase, Practice only (PLAN.md 2.8). */
class SessionHintsTest {

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

  @Test
  void anInterviewHasNoHints() {
    String id = sessions.create("tiny", SessionType.INTERVIEW).id();

    assertThatThrownBy(() -> sessions.hint(id, 1))
        .isInstanceOf(SessionException.class)
        .hasMessageContaining("Practice");
  }

  @Test
  void practiceRevealsTheLevelsInOrderAndRecordsEach() {
    String id = sessions.create("tiny", SessionType.PRACTICE).id();

    assertThat(sessions.hint(id, 1)).isEqualTo(new HintView(1, 1, "Phase 1 where."));
    assertThat(sessions.hint(id, 1).text()).isEqualTo("Phase 1 how.");
    assertThat(sessions.hint(id, 1).text()).isEqualTo("Phase 1 steps.");

    SessionView view = sessions.view(id);
    assertThat(view.hintsUsed()).containsEntry(1, 3);
    assertThat(view.events())
        .filteredOn(e -> e.type().equals("hint"))
        .extracting(e -> e.data().get("level"))
        .containsExactly(1, 2, 3);
  }

  @Test
  void thereIsNoFourthLevel() {
    String id = sessions.create("tiny", SessionType.PRACTICE).id();
    for (int i = 0; i < 3; i++) {
      sessions.hint(id, 1);
    }

    assertThatThrownBy(() -> sessions.hint(id, 1))
        .isInstanceOf(SessionException.class)
        .hasMessageContaining("three");
  }

  @Test
  void aLockedPhaseHasNoHintsYet() {
    String id = sessions.create("tiny", SessionType.PRACTICE).id();

    assertThatThrownBy(() -> sessions.hint(id, 2)).isInstanceOf(SessionException.class);
    sessions.skip(id);
    assertThat(sessions.hint(id, 2).text()).isEqualTo("Phase 2 where.");
  }

  @Test
  void revealedHintsSurviveAReload() {
    String id = sessions.create("tiny", SessionType.PRACTICE).id();
    sessions.hint(id, 1);
    sessions.hint(id, 1);

    assertThat(sessions.revealedHints(id, 1)).containsExactly("Phase 1 where.", "Phase 1 how.");
    assertThat(sessions.revealedHints(id, 2)).isEmpty();
  }
}
