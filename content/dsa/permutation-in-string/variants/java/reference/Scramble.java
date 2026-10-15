import java.util.Arrays;

/** Looks for rearrangements of a word inside a text. */
public class Scramble {

    /** Returns true if some stretch of the text is a rearrangement of the word. */
    static boolean appearsIn(String word, String text) {
        int m = word.length();
        if (m > text.length()) {
            return false; // no stretch is long enough
        }
        int[] need = new int[26];
        int[] have = new int[26];
        for (int i = 0; i < m; i++) {
            need[word.charAt(i) - 'a']++;
            have[text.charAt(i) - 'a']++;
        }
        if (Arrays.equals(need, have)) {
            return true;
        }
        for (int end = m; end < text.length(); end++) {
            have[text.charAt(end) - 'a']++; // enters the stretch
            have[text.charAt(end - m) - 'a']--; // leaves the stretch
            if (Arrays.equals(need, have)) {
                return true;
            }
        }
        return false;
    }
}
