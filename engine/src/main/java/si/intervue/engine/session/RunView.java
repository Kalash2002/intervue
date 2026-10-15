package si.intervue.engine.session;

import java.util.List;
import si.intervue.engine.languages.SourceDiagnostic;
import si.intervue.engine.sections.PhaseResult;

/**
 * One run as the room shows it: program output, compiler diagnostics for the editor, and the
 * results of the phases the target covered. {@code newlyPassed} lists phases that passed for the
 * first time in this run.
 */
public record RunView(
    String target,
    boolean compileError,
    boolean timedOut,
    String output,
    List<SourceDiagnostic> diagnostics,
    List<PhaseResult> phases,
    List<Integer> newlyPassed,
    int unlocked,
    int aiEditsActive) {}
