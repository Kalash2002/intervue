package si.intervue.engine.session;

import si.intervue.engine.sections.SessionType;

/**
 * One line of history. {@code countsAsSolved} is true only for an unassisted Interview session in
 * which every phase passed (PLAN.md 2.8); {@code total} and {@code label} are null until finish.
 */
public record SessionSummary(
    String id,
    String problemId,
    SessionType type,
    long startedAt,
    boolean finished,
    Long total,
    String label,
    boolean solved,
    boolean countsAsSolved) {}
