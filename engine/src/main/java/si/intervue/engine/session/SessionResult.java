package si.intervue.engine.session;

import java.util.List;
import si.intervue.engine.sections.PhaseResult;
import si.intervue.engine.sections.Score;

/** What the result page shows once a session is finished. */
public record SessionResult(
    Score score,
    List<PhaseResult> finalResults,
    boolean finalCompileError,
    TimelineReport report,
    String walkthrough,
    List<String> followUps) {}
