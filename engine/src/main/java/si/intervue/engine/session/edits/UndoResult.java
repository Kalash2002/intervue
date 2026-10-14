package si.intervue.engine.session.edits;

/**
 * Outcome of undoing one edit. When the file changed after the edit, nothing is undone and {@code
 * diff} shows the user's version against the version before the edit so they can decide.
 */
public record UndoResult(String id, boolean undone, String diff) {}
