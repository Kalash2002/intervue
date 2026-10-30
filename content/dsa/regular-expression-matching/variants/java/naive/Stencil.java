/** Matches texts against patterns of letters, dots and stars. */
public class Stencil {

    /** Returns whether pattern matches all of text. */
    static boolean matches(String text, String pattern) {
        return from(text, pattern, 0, 0);
    }

    /** Whether pattern from position j on matches all of text from position i on. */
    private static boolean from(String text, String pattern, int i, int j) {
        if (j == pattern.length()) {
            return i == text.length(); // the whole text must be used up
        }
        boolean first = i < text.length() && (pattern.charAt(j) == '.' || pattern.charAt(j) == text.charAt(i));
        if (j + 1 < pattern.length() && pattern.charAt(j + 1) == '*') {
            // try the piece matching no letter, then matching one more letter and staying in play
            return from(text, pattern, i, j + 2) || (first && from(text, pattern, i + 1, j));
        }
        return first && from(text, pattern, i + 1, j + 1);
    }
}
