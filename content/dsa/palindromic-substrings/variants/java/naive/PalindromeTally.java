/** Counts the stretches of a string that read the same both ways. */
public class PalindromeTally {

    /** Returns how many substrings of s, each position counted separately, are palindromes. */
    static int count(String s) {
        int total = 0;
        for (int i = 0; i < s.length(); i++) {
            for (int j = i; j < s.length(); j++) { // every substring s[i..j], single letters included
                if (isPalindrome(s, i, j)) {
                    total++;
                }
            }
        }
        return total;
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
