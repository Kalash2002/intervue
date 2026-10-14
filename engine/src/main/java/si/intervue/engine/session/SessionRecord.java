package si.intervue.engine.session;

import java.util.Map;
import si.intervue.engine.sections.SessionType;

/**
 * One row of {@code sessions}. {@code finishedAt} is null while the session runs; {@code hintsUsed}
 * maps a phase to the highest hint level revealed; result fields are JSON text.
 */
record SessionRecord(
    String id,
    String problemId,
    int problemVersion,
    String language,
    String sectionId,
    SessionType type,
    long startedAt,
    Long finishedAt,
    int minutes,
    boolean timed,
    long activeMs,
    int unlocked,
    boolean assisted,
    Map<Integer, Integer> hintsUsed,
    String finalResultsJson,
    String scoreJson,
    String notes) {

  boolean finished() {
    return finishedAt != null;
  }
}
