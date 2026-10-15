package si.intervue.engine.session.edits;

/** What happened to one proposed edit (docs/contracts/edit-protocol.md). */
public enum EditStatus {
  /** Written to the file; can be undone. */
  APPLIED,
  /**
   * Shown to the user but not written: the file changed, or the text did not match exactly once.
   */
  SUGGESTION,
  /**
   * Refused: a read-only, kit, data or outside path, a wrong extension, too large, or not UTF-8.
   */
  NOT_ALLOWED
}
