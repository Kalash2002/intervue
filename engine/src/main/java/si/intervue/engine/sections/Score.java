package si.intervue.engine.sections;

import java.util.List;

/**
 * A session's score. {@code label} is "Interview" or "Practice" (ADR 0006); {@code solved} means
 * every phase passed in the final run, whether or not the session counts as solved.
 */
public record Score(long total, String band, boolean solved, String label, List<ScorePart> parts) {

  /** One row of the score table. */
  public record ScorePart(String name, double points, double max, String note) {}
}
