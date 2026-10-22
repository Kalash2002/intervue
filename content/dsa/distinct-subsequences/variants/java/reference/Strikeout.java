/** Counts the ways to strike letters out of a text so that a given word is left. */
public class Strikeout {

    /** Returns the number of ways to choose positions of text that spell word, in order. */
    static int ways(String text, String word) {
        int m = word.length();
        int[] ways = new int[m + 1]; // ways[j]: ways to spell the first j letters of word from the text read so far
        ways[0] = 1; // the empty start of the word: strike out everything read so far
        for (int i = 0; i < text.length(); i++) {
            char letter = text.charAt(i);
            // downwards, so ways[j - 1] still leaves this letter unused and it is used once at most
            for (int j = m; j >= 1; j--) {
                if (word.charAt(j - 1) == letter) {
                    ways[j] += ways[j - 1]; // may wrap for short prefixes; ways[m] stays right modulo 2^32
                }
            }
        }
        return ways[m];
    }
}
