package si.intervue.engine.session;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;
import java.util.Map;
import org.junit.jupiter.api.Test;
import si.intervue.engine.sections.SessionType;
import si.intervue.problemformat.ProblemReader;

/** The session brief of a one-phase problem, which has no brief/intro.md. */
class SessionBriefTest {

  @Test
  void aOnePhaseProblemHasAnEmptyIntroAndItsStatementAsPhaseBrief() throws Exception {
    var folder = ProblemReader.read(Path.of(getClass().getResource("/dsa/tiny-dsa").toURI()));
    var session =
        new SessionRecord(
            "s",
            "tiny-dsa",
            1,
            "java",
            "dsa-quick",
            SessionType.PRACTICE,
            0,
            null,
            10,
            false,
            0,
            1,
            false,
            Map.of(),
            null,
            null,
            "");

    var brief = Sessions.brief(new Sessions.Context(session, folder, null, null, null));

    assertThat(brief).hasSize(2);
    assertThat(brief.get(0)).isEmpty();
    assertThat(brief.get(1)).contains("Add two numbers");
  }
}
