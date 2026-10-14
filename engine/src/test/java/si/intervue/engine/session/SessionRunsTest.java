package si.intervue.engine.session;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import si.intervue.engine.sections.SessionType;

/** Runs, unlocking and the read-only rule, against the real Java package. */
class SessionRunsTest {

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

  static void insertActiveEdit(EngineFixture engine, String sessionId) {
    String sql =
        "INSERT INTO edits(session_id, edit_id, reply_id, path, op, before_hash, after_hash)"
            + " VALUES (?, 'e1', 'r1', 'Calc.java', 'replace', 'sha256:a', 'sha256:b')";
    try (PreparedStatement ps = engine.db.connection().prepareStatement(sql)) {
      ps.setString(1, sessionId);
      ps.executeUpdate();
    } catch (SQLException e) {
      throw new IllegalStateException(e);
    }
  }

  @Test
  void aRunIsRecordedWithItsResultsAndActiveEdits() {
    String id = sessions.create("tiny", SessionType.INTERVIEW).id();

    RunView run = sessions.run(id, "phase:1");

    assertThat(run.compileError()).isFalse();
    assertThat(run.phases().get(0).failed()).isEqualTo(1);
    EventView event = last(id, "run");
    assertThat(event.data())
        .containsEntry("target", "phase:1")
        .containsEntry("failed", 1)
        .containsEntry("aiEditsActive", 0);
  }

  @Test
  void passingPhaseOneUnlocksPhaseTwoAndIsRecordedOnce() {
    String id = sessions.create("tiny", SessionType.INTERVIEW).id();
    sessions.writeFile(id, "Calc.java", EngineFixture.FIXED_CALC);

    RunView first = sessions.run(id, "phase:1");
    RunView second = sessions.run(id, "phase:1");

    assertThat(first.newlyPassed()).containsExactly(1);
    assertThat(first.unlocked()).isEqualTo(2);
    assertThat(second.newlyPassed()).isEmpty();
    assertThat(sessions.view(id).events())
        .extracting(EventView::type)
        .filteredOn("phasePassed"::equals)
        .hasSize(1);
    assertThat(sessions.view(id).brief().get(2)).isNotNull();
  }

  @Test
  void readOnlyFilesComeBackBeforeEveryRun() throws IOException {
    String id = sessions.create("tiny", SessionType.INTERVIEW).id();
    Path tests = engine.work(id).resolve("Phase1Tests.java");
    Files.writeString(
        tests, "import kit.Test;\npublic class Phase1Tests {\n  @Test void doubles() {}\n}\n");

    RunView run = sessions.run(id, "phase:1");

    assertThat(run.phases().get(0).failed()).isEqualTo(1);
    assertThat(Files.readString(tests))
        .isEqualTo(Files.readString(engine.source("Phase1Tests.java")));
  }

  @Test
  void aCompileErrorIsReportedWithDiagnostics() {
    String id = sessions.create("tiny", SessionType.INTERVIEW).id();
    sessions.writeFile(id, "Calc.java", "public class Calc {\n  int x = ;\n}\n");

    RunView run = sessions.run(id, "all");

    assertThat(run.compileError()).isTrue();
    assertThat(run.diagnostics()).anyMatch(d -> d.path().equals("Calc.java") && d.line() == 2);
    assertThat(last(id, "run").data()).containsEntry("compileError", true);
  }

  @Test
  void theMainTargetShowsOutputAndGradesNothing() {
    String id = sessions.create("tiny", SessionType.INTERVIEW).id();

    RunView run = sessions.run(id, "main");

    assertThat(run.output()).contains("5");
    assertThat(run.phases()).isEmpty();
  }

  @Test
  void refusesAnUnknownTarget() {
    String id = sessions.create("tiny", SessionType.INTERVIEW).id();

    assertThatThrownBy(() -> sessions.run(id, "phase:7")).isInstanceOf(SessionException.class);
  }

  @Test
  void anInterviewRunWithAnActiveAiEditMakesTheSessionAssisted() {
    String id = sessions.create("tiny", SessionType.INTERVIEW).id();
    insertActiveEdit(engine, id);

    RunView run = sessions.run(id, "phase:1");

    assertThat(run.aiEditsActive()).isEqualTo(1);
    assertThat(sessions.view(id).assisted()).isTrue();
  }

  private EventView last(String id, String type) {
    var events = sessions.view(id).events().stream().filter(e -> e.type().equals(type)).toList();
    return events.get(events.size() - 1);
  }
}
