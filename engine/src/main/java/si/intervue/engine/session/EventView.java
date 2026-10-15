package si.intervue.engine.session;

import java.util.Map;

/** A timeline event for the UI; {@code tMs} is active time since the session started. */
public record EventView(int seq, long tMs, String type, Map<String, Object> data) {}
