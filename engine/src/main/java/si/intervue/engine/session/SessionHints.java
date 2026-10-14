package si.intervue.engine.session;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import si.intervue.engine.sections.SessionType;
import si.intervue.engine.session.Sessions.Context;
import si.intervue.problemformat.HintLevels;

/** Practice hints: three authored levels per phase, revealed in order and recorded. */
final class SessionHints {

  private final Sessions sessions;
  private final SessionStore store;

  SessionHints(Sessions sessions, SessionStore store) {
    this.sessions = sessions;
    this.store = store;
  }

  /**
   * Reveals the next authored hint level of an unlocked phase. Practice only: Interview mode has no
   * hints at all, so no hint deductions exist in its score (PLAN.md 2.8).
   */
  HintView reveal(Context c, int phase) {
    String id = c.session().id();
    if (c.session().type() != SessionType.PRACTICE) {
      throw new SessionException("Hints are available in Practice sessions only.");
    }
    if (phase < 1 || phase > c.session().unlocked()) {
      throw new SessionException("Phase " + phase + " is not open yet.");
    }
    int level = c.session().hintsUsed().getOrDefault(phase, 0) + 1;
    if (level > HintLevels.LEVELS) {
      throw new SessionException("All three hints for phase " + phase + " are already shown.");
    }
    String text = hintLevels(c, phase).get(level);
    Map<Integer, Integer> used = new LinkedHashMap<>(c.session().hintsUsed());
    used.put(phase, level);
    store.setHintsUsed(id, used);
    sessions.event(c.session(), "hint", Map.of("phase", phase, "level", level));
    return new HintView(phase, level, text);
  }

  /** The hint texts already revealed for a phase, level 1 first. */
  List<String> revealed(Context c, int phase) {
    int shown = c.session().hintsUsed().getOrDefault(phase, 0);
    Map<Integer, String> levels = hintLevels(c, phase);
    List<String> texts = new ArrayList<>();
    for (int level = 1; level <= shown; level++) {
      texts.add(levels.get(level));
    }
    return texts;
  }

  private static Map<Integer, String> hintLevels(Context c, int phase) {
    var p = c.problem().problem().phases().get(phase - 1);
    return HintLevels.parse(Sessions.readString(c.problem().dir().resolve(p.hints())));
  }
}
