/** Finds the longest stretch of a string that reads the same both ways. */
public class PalindromeSpan {

    /** Returns the longest palindromic substring of s; on a tie, the one that starts first. */
    static String longest(String s) {
        int bestStart = 0;
        int bestLength = 1; // a single character is always a palindrome
        for (int centre = 0; centre < s.length(); centre++) {
            // right == centre: odd lengths around one character; centre + 1: even lengths around a gap
            for (int right = centre; right <= centre + 1; right++) {
                int lo = centre;
                int hi = right;
                while (lo >= 0 && hi < s.length() && s.charAt(lo) == s.charAt(hi)) {
                    lo--;
                    hi++;
                }
                int length = hi - lo - 1; // the loop stopped one step past each end
                // strictly longer only: on a tie the earlier centre, which starts earlier, stays
                if (length > bestLength) {
                    bestStart = lo + 1;
                    bestLength = length;
                }
            }
        }
        return s.substring(bestStart, bestStart + bestLength);
    }
}
