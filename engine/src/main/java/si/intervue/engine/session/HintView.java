package si.intervue.engine.session;

/** One revealed hint: the phase, its level (1 to 3) and the authored text. */
public record HintView(int phase, int level, String text) {}
