package si.intervue.sections.codinground;

import java.util.ArrayList;
import java.util.List;

/**
 * The arithmetic of {@code prototype/server/Server.java Sessions.score}, copied unchanged as a test
 * oracle so the new rule can be compared with it. Only the numbers are kept; notes and maps are
 * dropped.
 */
final class PrototypeScoreOracle {

  record Section(long passed, long failed) {}

  record Result(long total, String band, boolean solved, List<Double> points) {}

  private PrototypeScoreOracle() {}

  static Result score(
      List<Section> results,
      long minutes,
      long totalMs,
      int runs,
      int prompts,
      long longestGap,
      int promptStreak,
      int skipped) {
    int phases = results.size();
    double[] weights = phases == 3 ? new double[] {20, 30, 20} : new double[phases];
    if (phases != 3) {
      java.util.Arrays.fill(weights, 70.0 / Math.max(1, phases));
    }
    List<Double> points = new ArrayList<>();
    double correctness = 0;
    boolean solved = phases > 0;
    boolean phaseOne = false;
    for (int i = 0; i < phases; i++) {
      long passed = results.get(i).passed();
      long failed = results.get(i).failed();
      boolean green = passed > 0 && failed == 0;
      double fraction = passed + failed == 0 ? 0 : (double) passed / (passed + failed);
      double p = i == 0 ? (green ? weights[i] : 0) : weights[i] * fraction;
      if (i == 0) {
        phaseOne = green;
      }
      solved &= green;
      correctness += p;
      points.add(p);
    }
    long limit = minutes * 60_000;
    double timeBase =
        totalMs <= limit ? 10 : Math.max(0, 10 - Math.ceil((totalMs - limit) / 120_000.0));
    double time = timeBase * correctness / 70.0;
    points.add(time);
    double rhythm = 0;
    if (phaseOne && runs != 0) {
      rhythm += prompts == 0 || promptStreak <= 2 ? 8 : promptStreak == 3 ? 4 : 0;
      rhythm += longestGap <= 8 * 60_000 ? 8 : longestGap <= 12 * 60_000 ? 4 : 0;
      rhythm += skipped == 0 ? 4 : 0;
    }
    points.add(rhythm);
    long total = Math.round(correctness + time + rhythm);
    String band =
        total >= 85 && solved
            ? "Strong"
            : total >= 70 ? "Solid" : total >= 50 ? "Partial" : "Needs work";
    return new Result(total, band, solved, points);
  }
}
