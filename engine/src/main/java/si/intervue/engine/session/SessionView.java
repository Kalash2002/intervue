package si.intervue.engine.session;

import java.util.List;
import java.util.Map;
import si.intervue.engine.sections.SessionType;

/**
 * Everything the room and the result page show about one session. {@code brief} holds the intro
 * then one entry per phase, null while that phase is locked; {@code result} is null until finish.
 * {@code aiEditsApplied} and {@code aiEditsUndone} count the edit events (ADR 0006 shows them).
 */
public record SessionView(
    String id,
    String problemId,
    String title,
    SessionType type,
    int minutes,
    long activeMs,
    boolean finished,
    boolean assisted,
    int unlocked,
    int phases,
    Map<Integer, Long> phasesPassed,
    Map<Integer, Integer> hintsUsed,
    int aiEditsActive,
    int aiEditsApplied,
    int aiEditsUndone,
    List<String> brief,
    String notes,
    List<EventView> events,
    SessionResult result) {}
