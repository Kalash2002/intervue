import java.util.HashSet;
import java.util.Set;

/** Measures stretches of a string with no repeated character. */
public class Streaks {

    /** Returns the length of the longest stretch of consecutive characters with no repeats. */
    static int longestDistinct(String s) {
        int best = 0;
        for (int start = 0; start < s.length(); start++) {
            Set<Character> seen = new HashSet<>();
            int end = start;
            while (end < s.length() && seen.add(s.charAt(end))) {
                end++;
            }
            best = Math.max(best, end - start);
        }
        return best;
    }
}
