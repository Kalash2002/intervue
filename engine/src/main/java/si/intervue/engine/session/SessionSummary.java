package si.intervue.engine.session;

import si.intervue.engine.sections.SessionType;

/**
 * One line of history. {@code countsAsSolved} is true for an Interview session in which every phase
 * passed, with or without AI edits (ADR 0006); {@code total} and {@code label} are null until
 * finish.
 */
public record SessionSummary(
    String id,
    String problemId,
    String sectionId,
    SessionType type,
    long startedAt,
    boolean finished,
    Long total,
    String label,
    boolean solved,
    boolean countsAsSolved) {}
