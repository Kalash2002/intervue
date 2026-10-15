package si.intervue.engine.languages;

import java.nio.file.Path;
import si.intervue.problemformat.Variant;

/**
 * One run. {@code target} is {@code phase:<n>}, {@code all} or {@code main}; {@code limits} are
 * already scaled by the calibration factor, which the kit applies to its per-test limits.
 */
public record RunRequest(
    Path workDir,
    Path outDir,
    String target,
    Variant variant,
    RunLimits limits,
    double calibrationFactor) {}
