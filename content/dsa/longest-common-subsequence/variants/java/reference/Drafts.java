/** Compares two strings letter by letter. */
public class Drafts {

    /** Returns the length of the longest string that is a subsequence of both first and second. */
    static int longestCommon(String first, String second) {
        // prev[j]: the answer for the first i - 1 letters of first and the first j letters of second
        int[] prev = new int[second.length() + 1];
        int[] cur = new int[second.length() + 1]; // cur[0] stays 0: an empty prefix shares nothing
        for (int i = 1; i <= first.length(); i++) {
            for (int j = 1; j <= second.length(); j++) {
                if (first.charAt(i - 1) == second.charAt(j - 1)) {
                    cur[j] = prev[j - 1] + 1; // keep the two equal last letters as a pair
                } else {
                    cur[j] = Math.max(prev[j], cur[j - 1]); // drop the last letter of one string or the other
                }
            }
            int[] done = prev;
            prev = cur;
            cur = done; // reused for the next row; every entry but [0] is overwritten
        }
        return prev[second.length()];
    }
}
