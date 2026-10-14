package si.intervue.engine.languages;

import java.util.List;

/** Outcome of a compile; {@code output} is the text shown in the output pane when it failed. */
public record CompileResult(boolean ok, String output, List<SourceDiagnostic> diagnostics) {}
