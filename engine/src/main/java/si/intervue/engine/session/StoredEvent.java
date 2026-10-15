package si.intervue.engine.session;

import java.util.Map;

/** One row of {@code events}: {@code tMs} is active time since the start; data is JSON fields. */
record StoredEvent(int seq, long tMs, String type, Map<String, Object> data) {

  long number(String key) {
    return data.get(key) instanceof Number n ? n.longValue() : 0;
  }
}
