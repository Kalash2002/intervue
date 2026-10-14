package si.intervue.problemformat;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Splits a {@code hints/phase<N>.md} file into its levels: the text under each {@code ## Level n}
 * heading, trimmed. Used by the validator and by the engine when a hint is revealed.
 */
public final class HintLevels {

  public static final int LEVELS = 3;
  private static final Pattern HEADING = Pattern.compile("(?m)^## Level ([1-3])[ \\t]*$");

  private HintLevels() {}

  /** Level number to its text, in file order; a level without a heading is absent. */
  public static Map<Integer, String> parse(String text) {
    Map<Integer, String> levels = new LinkedHashMap<>();
    Matcher heading = HEADING.matcher(text);
    Integer level = null;
    int bodyStart = 0;
    while (heading.find()) {
      if (level != null) {
        levels.putIfAbsent(level, text.substring(bodyStart, heading.start()).strip());
      }
      level = Integer.parseInt(heading.group(1));
      bodyStart = heading.end();
    }
    if (level != null) {
      levels.putIfAbsent(level, text.substring(bodyStart).strip());
    }
    return levels;
  }
}
