package si.intervue.engine.session;

/** One file in a session's project; {@code path} uses forward slashes. */
public record FileEntry(String path, boolean readOnly) {}
