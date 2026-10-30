/** Matches texts against patterns of letters, dots and stars. */
public class Stencil {

    /** Returns whether pattern matches all of text. */
    static boolean matches(String text, String pattern) {
        int n = text.length();
        int m = pattern.length();
        // next[j]: whether text from i + 1 on matches pattern from j on; cur[j]: the same from i on
        boolean[] next = new boolean[m + 1]; // all false: past the end of text, nothing has a next letter
        boolean[] cur = new boolean[m + 1];
        for (int i = n; i >= 0; i--) {
            cur[m] = i == n; // the empty rest of the pattern matches only the empty rest of the text
            for (int j = m - 1; j >= 0; j--) {
                boolean first = i < n && (pattern.charAt(j) == '.' || pattern.charAt(j) == text.charAt(i));
                if (j + 1 < m && pattern.charAt(j + 1) == '*') {
                    // the piece matches no letter, or one more letter and stays in play
                    cur[j] = cur[j + 2] || (first && next[j]);
                } else {
                    cur[j] = first && next[j + 1]; // a single letter or dot; a cell under a star is never read
                }
            }
            boolean[] done = next;
            next = cur;
            cur = done; // reused for the next row; every entry is overwritten
        }
        return next[0];
    }
}
