package si.intervue.engine.session;

import java.util.List;

/**
 * Numbers that describe how a session went, read from its timeline as the prototype's report did;
 * the score and the reviewer use them. Times are active milliseconds.
 */
public record TimelineReport(
    long activeMs,
    int runs,
    int compileErrors,
    int prompts,
    long firstRunMs,
    long firstPromptMs,
    long longestGapWithoutRunMs,
    int longestPromptStreak,
    int phasesSkipped) {

  static TimelineReport of(List<StoredEvent> events, long activeMs) {
    int runs = 0;
    int compileErrors = 0;
    int prompts = 0;
    long firstRun = -1;
    long firstPrompt = -1;
    long lastRun = 0;
    long longestGap = 0;
    int streak = 0;
    int longestStreak = 0;
    int skipped = 0;
    for (StoredEvent e : events) {
      switch (e.type()) {
        case "run" -> {
          runs++;
          compileErrors += Boolean.TRUE.equals(e.data().get("compileError")) ? 1 : 0;
          firstRun = firstRun < 0 ? e.tMs() : firstRun;
          longestGap = Math.max(longestGap, e.tMs() - lastRun);
          lastRun = e.tMs();
          streak = 0;
        }
        case "chat" -> {
          prompts++;
          firstPrompt = firstPrompt < 0 ? e.tMs() : firstPrompt;
          longestStreak = Math.max(longestStreak, ++streak);
        }
        case "skip" -> skipped++;
        default -> {
          // Other events (hints, edits, pauses) do not change these numbers.
        }
      }
    }
    longestGap = Math.max(longestGap, activeMs - lastRun);
    return new TimelineReport(
        activeMs,
        runs,
        compileErrors,
        prompts,
        firstRun,
        firstPrompt,
        longestGap,
        longestStreak,
        skipped);
  }
}
