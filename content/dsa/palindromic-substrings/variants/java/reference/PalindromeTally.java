/** Counts the stretches of a string that read the same both ways. */
public class PalindromeTally {

    /** Returns how many substrings of s, each position counted separately, are palindromes. */
    static int count(String s) {
        int total = 0;
        for (int centre = 0; centre < s.length(); centre++) {
            total += grow(s, centre, centre); // odd lengths: the middle is a letter
            total += grow(s, centre, centre + 1); // even lengths: the middle is the gap after it
        }
        return total;
    }

    /** Counts the palindromes with this middle: one for each step outward while the ends match. */
    private static int grow(String s, int lo, int hi) {
        int found = 0;
        while (lo >= 0 && hi < s.length() && s.charAt(lo) == s.charAt(hi)) {
            found++;
            lo--;
            hi++;
        }
        return found;
    }
}
