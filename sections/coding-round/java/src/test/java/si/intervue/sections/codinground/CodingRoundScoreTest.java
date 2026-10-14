package si.intervue.sections.codinground;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.Test;
import si.intervue.engine.sections.PhaseResult;
import si.intervue.engine.sections.Score;
import si.intervue.engine.sections.ScoreInput;
import si.intervue.engine.sections.SessionType;

class CodingRoundScoreTest {

  private static final int[] WEIGHTS = {20, 30, 20};
  private final CodingRoundScore rule = new CodingRoundScore();

  private static ScoreInput interview(List<PhaseResult> results, long activeMs, boolean assisted) {
    return new ScoreInput(
        SessionType.INTERVIEW, assisted, 60, results, activeMs, 12, 5, 300_000, 2, 0);
  }

  private static PhaseResult phase(int id, int passed, int failed) {
    return new PhaseResult(id, "Phase " + id, WEIGHTS[id - 1], id == 1, passed, failed, 0, 0);
  }

  @Test
  void matchesThePrototypeOnRandomSessions() {
    Random random = new Random(7);
    for (int i = 0; i < 2_000; i++) {
      List<PhaseResult> results = new ArrayList<>();
      List<PrototypeScoreOracle.Section> sections = new ArrayList<>();
      for (int p = 1; p <= 3; p++) {
        int passed = random.nextInt(8);
        int failed = random.nextInt(4);
        results.add(phase(p, passed, failed));
        sections.add(new PrototypeScoreOracle.Section(passed, failed));
      }
      long activeMs = random.nextLong(30 * 60_000, 120 * 60_000);
      int runs = random.nextInt(4);
      int prompts = random.nextInt(10);
      long gap = random.nextLong(20 * 60_000);
      int streak = random.nextInt(6);
      int skipped = random.nextInt(2);

      Score score =
          rule.score(
              new ScoreInput(
                  SessionType.INTERVIEW,
                  false,
                  60,
                  results,
                  activeMs,
                  runs,
                  prompts,
                  gap,
                  streak,
                  skipped));
      PrototypeScoreOracle.Result expected =
          PrototypeScoreOracle.score(sections, 60, activeMs, runs, prompts, gap, streak, skipped);

      assertThat(score.total()).as("total, case %d", i).isEqualTo(expected.total());
      assertThat(score.band()).as("band, case %d", i).isEqualTo(expected.band());
      assertThat(score.solved()).as("solved, case %d", i).isEqualTo(expected.solved());
      for (int part = 0; part < expected.points().size(); part++) {
        assertThat(score.parts().get(part).points())
            .isCloseTo(expected.points().get(part), within(1e-9));
      }
    }
  }

  @Test
  void readsPhaseWeightsFromTheProblem() {
    List<PhaseResult> twoPhases =
        List.of(
            new PhaseResult(1, "Fix", 40, true, 3, 0, 0, 0),
            new PhaseResult(2, "Build", 30, false, 1, 1, 0, 0));
    Score score = rule.score(interview(twoPhases, 10 * 60_000, false));

    assertThat(score.parts().get(0).max()).isEqualTo(40);
    assertThat(score.parts().get(1).points()).isEqualTo(15);
  }

  @Test
  void countsTimeoutsAndUnrunTestsAgainstAPhase() {
    PhaseResult stopped = new PhaseResult(2, "Phase 2", 30, false, 2, 0, 1, 1);
    Score score =
        rule.score(interview(List.of(phase(1, 3, 0), stopped, phase(3, 0, 0)), 10 * 60_000, false));

    assertThat(score.parts().get(1).points()).isEqualTo(15);
  }

  @Test
  void anAssistedInterviewIsStillLabelledInterview() {
    List<PhaseResult> all = List.of(phase(1, 3, 0), phase(2, 3, 0), phase(3, 3, 0));

    assertThat(rule.score(interview(all, 30 * 60_000, false)).label()).isEqualTo("Interview");
    Score assisted = rule.score(interview(all, 30 * 60_000, true));
    // ADR 0006: using the assistant never changes the label.
    assertThat(assisted.label()).isEqualTo("Interview");
    assertThat(assisted.total()).isEqualTo(100);
  }

  @Test
  void practiceScoresCorrectnessAndPhasesOnly() {
    List<PhaseResult> results = List.of(phase(1, 3, 0), phase(2, 1, 1), phase(3, 0, 2));
    Score score =
        rule.score(
            new ScoreInput(SessionType.PRACTICE, false, 60, results, 500 * 60_000, 0, 40, 0, 9, 1));

    assertThat(score.label()).isEqualTo("Practice");
    assertThat(score.parts())
        .extracting(Score.ScorePart::name)
        .noneMatch(n -> n.equals("Time") || n.equals("Working rhythm"));
    assertThat(score.total()).isEqualTo(35);
    assertThat(score.band()).isNull();
  }
}
