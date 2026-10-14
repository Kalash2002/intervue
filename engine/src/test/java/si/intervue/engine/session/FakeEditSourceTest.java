package si.intervue.engine.session;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.nio.file.Path;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import si.intervue.engine.sections.SessionType;
import si.intervue.engine.session.edits.EditStatus;
import si.intervue.engine.session.edits.FakeEditSource;

/** The recorded Write code replies that stand in for the backend until Phase 1b. */
class FakeEditSourceTest {

  @TempDir Path root;
  EngineFixture engine;

  @BeforeEach
  void setUp() {
    engine = new EngineFixture(root);
    Path repoProblems = Path.of(System.getProperty("intervue.content"), "problems");
    EngineFixture.copyTree(
        repoProblems.resolve("hold-exposure"), engine.problems.resolve("hold-exposure"));
  }

  @AfterEach
  void tearDown() {
    engine.close();
  }

  @Test
  void theHoldExposureDemoFixesPhaseOneAndMakesTheInterviewAssisted() {
    Sessions sessions = engine.sessions;
    String id = sessions.create("hold-exposure", SessionType.INTERVIEW).id();

    var reply = sessions.applyEdits(id, FakeEditSource.lines("hold-exposure-phase1"));
    RunView run = sessions.run(id, "phase:1");

    assertThat(reply.outcomes()).extracting(o -> o.status()).containsExactly(EditStatus.APPLIED);
    assertThat(reply.note()).contains("long");
    assertThat(run.newlyPassed()).containsExactly(1);
    assertThat(sessions.view(id).assisted()).isTrue();
  }

  @Test
  void onlyBundledStreamsChosenByIdCanBeReplayed() {
    assertThat(FakeEditSource.ids()).contains("hold-exposure-phase1");
    assertThatThrownBy(() -> FakeEditSource.lines("../db/migration/V1__init.sql"))
        .isInstanceOf(IllegalArgumentException.class);
    assertThatThrownBy(() -> FakeEditSource.lines("no-such-stream"))
        .isInstanceOf(IllegalArgumentException.class);
  }
}
