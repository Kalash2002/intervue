package si.intervue.engine.sections;

/** Turns a finished session's facts into a score. */
public interface ScoringRule {
  Score score(ScoreInput input);
}
