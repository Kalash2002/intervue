/** Compares two strings letter by letter. */
public class Drafts {

    /** Returns the length of the longest string that is a subsequence of both first and second. */
    static int longestCommon(String first, String second) {
        return from(first, second, 0, 0);
    }

    /** The answer for first from position i on and second from position j on. */
    private static int from(String first, String second, int i, int j) {
        if (i == first.length() || j == second.length()) {
            return 0;
        }
        if (first.charAt(i) == second.charAt(j)) {
            return 1 + from(first, second, i + 1, j + 1); // pairing equal first letters is never worse
        }
        // the two first letters differ, so at least one of them is not in the answer
        return Math.max(from(first, second, i + 1, j), from(first, second, i, j + 1));
    }
}
