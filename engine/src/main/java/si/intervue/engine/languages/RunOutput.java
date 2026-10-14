package si.intervue.engine.languages;

import java.nio.file.Path;

/**
 * Outcome of a run. {@code output} is the program's text for the output pane; {@code resultsFile}
 * is null for the {@code main} target, which has no tests.
 */
public record RunOutput(String output, int exitCode, boolean timedOut, Path resultsFile) {}
