/** Finds the longest stretch of a string that reads the same both ways. */
public class PalindromeSpan {

    /** Returns the longest palindromic substring of s; on a tie, the one that starts first. */
    static String longest(String s) {
        int bestStart = 0;
        int bestLength = 1; // a single character is always a palindrome
        for (int i = 0; i < s.length(); i++) {
            // only substrings longer than the best so far can replace it, so a tie keeps the first
            for (int j = i + bestLength; j < s.length(); j++) {
                if (isPalindrome(s, i, j)) {
                    bestStart = i;
                    bestLength = j - i + 1;
                }
            }
        }
        return s.substring(bestStart, bestStart + bestLength);
    }

    private static boolean isPalindrome(String s, int i, int j) {
        while (i < j) {
            if (s.charAt(i++) != s.charAt(j--)) {
                return false;
            }
        }
        return true;
    }
}
