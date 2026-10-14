package si.intervue.engine.languages;

/**
 * A compiler message tied to a place in a work file; {@code path} is relative to the work folder,
 * {@code line} and {@code column} are 1-based.
 */
public record SourceDiagnostic(
    String path, long line, long column, String severity, String message) {}
