/** Measures stretches of a string with no repeated character. */
public class Streaks {

    /** Returns the length of the longest stretch of consecutive characters with no repeats. */
    static int longestDistinct(String s) {
        int[] after = new int[65_536]; // position just after the last copy of each character, 0 if unseen
        int start = 0;
        int best = 0;
        for (int end = 0; end < s.length(); end++) {
            char c = s.charAt(end);
            start = Math.max(start, after[c]); // never move the start back
            after[c] = end + 1;
            best = Math.max(best, end - start + 1);
        }
        return best;
    }
}
