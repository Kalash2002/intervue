package si.intervue.engine.sections;

import java.util.List;

/**
 * The facts a score is computed from, gathered by the engine at finish. {@code activeMs} excludes
 * paused time; {@code promptStreak} is the most prompts sent in a row without a run.
 */
public record ScoreInput(
    SessionType sessionType,
    boolean assisted,
    int minutes,
    List<PhaseResult> finalResults,
    long activeMs,
    int runs,
    int prompts,
    long longestGapMs,
    int promptStreak,
    int skipped) {}
