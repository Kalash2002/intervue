package si.intervue.engine.session;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import si.intervue.engine.languages.SourceSymbol;
import si.intervue.engine.sections.SessionType;

/** Editor tier 1 through the engine: routed to the session's language package. */
class SessionEditorTest {

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
  void symbolsComeFromTheEditableFilesOnly() {
    String id = engine.sessions.create("tiny", SessionType.INTERVIEW).id();

    assertThat(engine.sessions.symbols(id))
        .extracting(SourceSymbol::name)
        .contains("Calc", "twice", "sum", "Main")
        .doesNotContain("Phase1Tests", "doubles", "TestRunner");
  }

  @Test
  void diagnosticsPointAtTheCandidatesFileAndNeverAtTheKit() {
    String id = engine.sessions.create("tiny", SessionType.INTERVIEW).id();
    engine.sessions.writeFile(
        id,
        "Calc.java",
        "public class Calc {\n  static int twice(int n) {\n    return m;\n  }\n}\n");

    assertThat(engine.sessions.diagnostics(id))
        .isNotEmpty()
        .allMatch(d -> !d.path().startsWith("kit/"))
        .anyMatch(d -> d.path().equals("Calc.java") && d.line() == 3);
  }
}
