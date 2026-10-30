// The DSA scoring rule (ADR 0008): correctness 80 in proportion to tests passed, time 20 only when
// every test passed. Must not read the clock: a timeout is a test result, not a time penalty.
package si.intervue.sections.dsaquick;

import java.util.List;
import si.intervue.engine.sections.PhaseResult;
import si.intervue.engine.sections.Score;
import si.intervue.engine.sections.Score.ScorePart;
import si.intervue.engine.sections.ScoreInput;
import si.intervue.engine.sections.ScoringRule;
import si.intervue.engine.sections.SessionType;

final class DsaScore implements ScoringRule {

  private static final double CORRECTNESS = 80;
  private static final double TIME = 20;

  @Override
  public Score score(ScoreInput input) {
    // One phase; failures, timeouts and tests never reported all count as not passed.
    PhaseResult phase = input.finalResults().get(0);
    double correctness = phase.total() == 0 ? 0 : CORRECTNESS * phase.passed() / phase.total();
    boolean solved = phase.green();
    double time = solved ? TIME : 0;
    long total = Math.round(correctness + time);
    // ADR 0006: using the assistant never changes the label; assisted is shown on its own.
    boolean practice = input.sessionType() == SessionType.PRACTICE;
    return new Score(
        total,
        practice ? null : band(total, solved),
        solved,
        practice ? "Practice" : "Interview",
        List.of(
            new ScorePart(
                "Correctness",
                correctness,
                CORRECTNESS,
                phase.passed() + " of " + phase.total() + " tests pass in the final run"),
            new ScorePart(
                "Time",
                time,
                TIME,
                solved ? "Every test passed" : "Counted only when every test passes")));
  }

  // "Strong" is reserved for a solved question, as in the coding round.
  private static String band(long total, boolean solved) {
    if (total >= 85 && solved) {
      return "Strong";
    }
    return total >= 70 ? "Solid" : total >= 50 ? "Partial" : "Needs work";
  }
}
