package si.intervue.engine.session;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import si.intervue.engine.sections.SessionType;

/** Creating a session and the rules for its files (PLAN.md 2.8: the source is never modified). */
class SessionFilesTest {

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
  void createsAWorkingCopyWithTheKitAndRecordsTheType() {
    SessionView view = sessions.create("tiny", SessionType.PRACTICE);

    Path work = engine.work(view.id());
    assertThat(work.resolve("Calc.java")).exists();
    assertThat(work.resolve("data/numbers.txt")).exists();
    assertThat(work.resolve("kit/TestRunner.java")).exists();
    assertThat(view.type()).isEqualTo(SessionType.PRACTICE);
    assertThat(view.unlocked()).isEqualTo(1);
    assertThat(view.minutes()).isEqualTo(10);
  }

  @Test
  void refusesAnUnknownProblem() {
    assertThatThrownBy(() -> sessions.create("nope", SessionType.INTERVIEW))
        .isInstanceOf(SessionException.class);
  }

  @Test
  void listsProjectFilesMarkingReadOnlyAndHidingTheKit() {
    String id = sessions.create("tiny", SessionType.INTERVIEW).id();

    assertThat(sessions.files(id))
        .extracting(FileEntry::path, FileEntry::readOnly)
        .containsExactly(
            org.assertj.core.groups.Tuple.tuple("Calc.java", false),
            org.assertj.core.groups.Tuple.tuple("Main.java", false),
            org.assertj.core.groups.Tuple.tuple("Phase1Tests.java", true),
            org.assertj.core.groups.Tuple.tuple("Phase2Tests.java", true),
            org.assertj.core.groups.Tuple.tuple("data/numbers.txt", true));
  }

  @Test
  void writesAnEditableFileAndNeverTouchesTheSource() throws IOException {
    String id = sessions.create("tiny", SessionType.INTERVIEW).id();
    String original = Files.readString(engine.source("Calc.java"));

    sessions.writeFile(id, "Calc.java", EngineFixture.FIXED_CALC);

    assertThat(sessions.readFile(id, "Calc.java")).isEqualTo(EngineFixture.FIXED_CALC);
    assertThat(Files.readString(engine.source("Calc.java"))).isEqualTo(original);
  }

  @Test
  void refusesToWriteReadOnlyKitUnknownOrEscapingPaths() {
    String id = sessions.create("tiny", SessionType.INTERVIEW).id();

    for (String path :
        new String[] {
          "Phase1Tests.java",
          "data/numbers.txt",
          "kit/Check.java",
          "Nope.java",
          "../x.java",
          "/etc/passwd",
          "./Phase1Tests.java",
          "data//numbers.txt",
          "./kit/Check.java"
        }) {
      assertThatThrownBy(() -> sessions.writeFile(id, path, "x"))
          .as(path)
          .isInstanceOf(SessionException.class);
    }
  }

  @Test
  void createsNewSourceFilesWithAllowedExtensionsOnly() {
    String id = sessions.create("tiny", SessionType.INTERVIEW).id();

    sessions.createFile(id, "Index.java");
    assertThat(sessions.readFile(id, "Index.java")).contains("public class Index");
    assertThat(sessions.files(id)).extracting(FileEntry::path).contains("Index.java");
    assertThatThrownBy(() -> sessions.createFile(id, "run.sh"))
        .isInstanceOf(SessionException.class);
    assertThatThrownBy(() -> sessions.createFile(id, "Calc.java"))
        .isInstanceOf(SessionException.class);
    assertThatThrownBy(() -> sessions.createFile(id, "kit/Evil.java"))
        .isInstanceOf(SessionException.class);
    // Projects are flat: the runner compiles top-level sources only.
    for (String path : new String[] {"./kit/Evil.java", "util/Parser.java", "./New.java"}) {
      assertThatThrownBy(() -> sessions.createFile(id, path))
          .as(path)
          .isInstanceOf(SessionException.class);
    }
  }

  @Test
  void showsOnlyUnlockedPhaseBriefs() {
    SessionView view = sessions.create("tiny", SessionType.INTERVIEW);

    assertThat(view.brief()).hasSize(3);
    assertThat(view.brief().get(0)).contains("intro");
    assertThat(view.brief().get(1)).contains("phase1");
    assertThat(view.brief().get(2)).isNull();
  }

  @Test
  void savesNotes() {
    String id = sessions.create("tiny", SessionType.INTERVIEW).id();

    sessions.saveNotes(id, "remember the overflow");

    assertThat(sessions.view(id).notes()).isEqualTo("remember the overflow");
  }

  @Test
  void theViewNamesEveryPhaseForTheOverview() {
    assertThat(sessions.create("tiny", SessionType.INTERVIEW).phaseTitles())
        .containsExactly("Fix", "Grow");
  }

  @Test
  void practiceIsUntimedUnlessAskedAndInterviewIsAlwaysTimed() {
    assertThat(sessions.create("tiny", SessionType.PRACTICE).timed()).isFalse();
    assertThat(sessions.create("tiny", SessionType.PRACTICE, true).timed()).isTrue();
    assertThat(sessions.create("tiny", SessionType.INTERVIEW, false).timed()).isTrue();
  }
}
