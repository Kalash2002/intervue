package si.intervue.engine.session;

import java.util.List;

/**
 * Active time under PLAN.md 2.8's pause rule: wall time since the start minus every gap between a
 * {@code pause} and its {@code resume}. Those two events carry the wall time in {@code at}.
 */
final class SessionClock {

  static final String PAUSE = "pause";
  static final String RESUME = "resume";

  private SessionClock() {}

  static long activeMs(long startedAt, List<StoredEvent> events, long nowWall) {
    return Math.max(0, nowWall - startedAt - pausedMs(events, nowWall));
  }

  static long pausedMs(List<StoredEvent> events, long nowWall) {
    long paused = 0;
    Long pausedAt = null;
    for (StoredEvent e : events) {
      if (e.type().equals(PAUSE) && pausedAt == null) {
        pausedAt = e.number("at");
      } else if (e.type().equals(RESUME) && pausedAt != null) {
        paused += e.number("at") - pausedAt;
        pausedAt = null;
      }
    }
    // Still paused (between a clean shutdown and the next start): time stands still.
    return pausedAt == null ? paused : paused + (nowWall - pausedAt);
  }

  static boolean isPaused(List<StoredEvent> events) {
    for (int i = events.size() - 1; i >= 0; i--) {
      String type = events.get(i).type();
      if (type.equals(PAUSE)) {
        return true;
      }
      if (type.equals(RESUME)) {
        return false;
      }
    }
    return false;
  }
}
