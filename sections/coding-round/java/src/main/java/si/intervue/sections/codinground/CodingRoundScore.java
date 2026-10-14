package si.intervue.sections.codinground;

import java.util.ArrayList;
import java.util.List;
import si.intervue.engine.sections.PhaseResult;
import si.intervue.engine.sections.Score;
import si.intervue.engine.sections.Score.ScorePart;
import si.intervue.engine.sections.ScoreInput;
import si.intervue.engine.sections.ScoringRule;
import si.intervue.engine.sections.SessionType;

/**
 * The prototype's formula (correctness 70 by phase weight, time 10, working rhythm 20) with weights
 * read from the problem; Practice keeps only correctness. CodingRoundScoreTest checks it against a
 * copy of the prototype's arithmetic.
 */
final class CodingRoundScore implements ScoringRule {

  private static final double CORRECTNESS = 70;
  private static final double TIME = 10;
  private static final long MINUTE_MS = 60_000;

  @Override
  public Score score(ScoreInput input) {
    List<ScorePart> parts = new ArrayList<>();
    double correctness = 0;
    boolean solved = !input.finalResults().isEmpty();
    for (PhaseResult phase : input.finalResults()) {
      double points = phasePoints(phase);
      correctness += points;
      solved &= phase.green();
      parts.add(
          new ScorePart(
              "Phase " + phase.phaseId() + ": " + phase.title(),
              points,
              phase.weight(),
              phaseNote(phase)));
    }
    if (input.sessionType() == SessionType.PRACTICE) {
      return new Score(Math.round(correctness), null, solved, "Practice", parts);
    }
    double time = timePoints(input, correctness);
    parts.add(new ScorePart("Time", time, TIME, timeNote(input)));
    boolean phaseOneGreen = !input.finalResults().isEmpty() && input.finalResults().get(0).green();
    double rhythm = phaseOneGreen && input.runs() > 0 ? rhythmPoints(input) : 0;
    parts.add(
        new ScorePart(
            "Working rhythm",
            rhythm,
            20,
            phaseOneGreen && input.runs() > 0
                ? "8 for never sending more than two prompts without a run, 8 for never going more than 8"
                    + " minutes without a run, 4 for not skipping a phase"
                : "Counted once phase 1 passes"));
    long total = Math.round(correctness + time + rhythm);
    String label = input.assisted() ? "Interview, assisted" : "Interview";
    return new Score(total, band(total, solved), solved, label, parts);
  }

  /** All-or-nothing phases score only when every test passes; others in proportion. */
  private static double phasePoints(PhaseResult phase) {
    if (phase.allOrNothing()) {
      return phase.green() ? phase.weight() : 0;
    }
    return phase.total() == 0 ? 0 : phase.weight() * (double) phase.passed() / phase.total();
  }

  private static String phaseNote(PhaseResult phase) {
    String note = phase.passed() + " of " + phase.total() + " tests pass in the final run";
    return phase.allOrNothing() && !phase.green()
        ? note + "; this phase scores only when all pass"
        : note;
  }

  // One point lost for every started two minutes over the limit, scaled by how much was solved.
  private static double timePoints(ScoreInput input, double correctness) {
    long limit = input.minutes() * MINUTE_MS;
    long over = input.activeMs() - limit;
    double base = over <= 0 ? TIME : Math.max(0, TIME - Math.ceil(over / 120_000.0));
    return base * correctness / CORRECTNESS;
  }

  private static String timeNote(ScoreInput input) {
    return input.activeMs() <= input.minutes() * MINUTE_MS
        ? "Finished inside the limit; scaled by how much was solved"
        : "Over the limit; one point lost for every two minutes";
  }

  private static double rhythmPoints(ScoreInput input) {
    double rhythm =
        input.prompts() == 0 || input.promptStreak() <= 2 ? 8 : input.promptStreak() == 3 ? 4 : 0;
    rhythm +=
        input.longestGapMs() <= 8 * MINUTE_MS ? 8 : input.longestGapMs() <= 12 * MINUTE_MS ? 4 : 0;
    rhythm += input.skipped() == 0 ? 4 : 0;
    return rhythm;
  }

  // "Strong" is reserved for sessions where every phase passes.
  private static String band(long total, boolean solved) {
    if (total >= 85 && solved) {
      return "Strong";
    }
    return total >= 70 ? "Solid" : total >= 50 ? "Partial" : "Needs work";
  }
}
