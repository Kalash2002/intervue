package si.intervue.sections.dsaquick;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import java.util.List;
import org.junit.jupiter.api.Test;
import si.intervue.engine.sections.PhaseResult;
import si.intervue.engine.sections.Score;
import si.intervue.engine.sections.ScoreInput;
import si.intervue.engine.sections.SessionType;

class DsaScoreTest {

  private static Score score(SessionType type, boolean assisted, int passed, int failed, int out) {
    PhaseResult phase = new PhaseResult(1, "Solve", 80, true, passed, failed, out, 0);
    return new DsaScore()
        .score(new ScoreInput(type, assisted, 20, List.of(phase), 60_000, 1, 0, 0, 0, 0));
  }

  @Test
  void everyTestPassedIsAHundredAndSolved() {
    Score s = score(SessionType.INTERVIEW, false, 6, 0, 0);

    assertThat(s.total()).isEqualTo(100);
    assertThat(s.solved()).isTrue();
    assertThat(s.band()).isEqualTo("Strong");
    assertThat(s.label()).isEqualTo("Interview");
  }

  @Test
  void timeoutsCountAsNotPassedAndEarnNoTime() {
    Score s = score(SessionType.INTERVIEW, false, 4, 0, 2);

    assertThat(s.parts().get(0).points()).isCloseTo(53.33, within(0.01));
    assertThat(s.parts().get(1).points()).isZero();
    assertThat(s.total()).isEqualTo(53);
    assertThat(s.solved()).isFalse();
    assertThat(s.band()).isEqualTo("Partial");
  }

  @Test
  void failuresScoreTheSameAsTimeouts() {
    assertThat(score(SessionType.INTERVIEW, false, 4, 2, 0).total()).isEqualTo(53);
  }

  @Test
  void aRunThatDidNotCompileScoresZeroAndKeepsItsLabel() {
    PhaseResult none = new PhaseResult(1, "Solve", 80, true, 0, 0, 0, 0);
    Score s =
        new DsaScore()
            .score(
                new ScoreInput(SessionType.PRACTICE, false, 20, List.of(none), 0, 0, 0, 0, 0, 0));

    assertThat(s.total()).isZero();
    assertThat(s.solved()).isFalse();
    assertThat(s.label()).isEqualTo("Practice");
  }

  @Test
  void practiceAndAssistedKeepTheirLabels() {
    assertThat(score(SessionType.PRACTICE, false, 6, 0, 0).label()).isEqualTo("Practice");
    // ADR 0006: using the assistant never changes the label or the solved flag.
    Score assisted = score(SessionType.INTERVIEW, true, 6, 0, 0);
    assertThat(assisted.label()).isEqualTo("Interview");
    assertThat(assisted.solved()).isTrue();
  }
}
