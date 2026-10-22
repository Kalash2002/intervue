/** Counts the ways to strike letters out of a text so that a given word is left. */
public class Strikeout {

    /** Returns the number of ways to choose positions of text that spell word, in order. */
    static int ways(String text, String word) {
        return from(text, word, 0, 0);
    }

    /** Ways to spell word from position j on with the letters of text from position i on. */
    private static int from(String text, String word, int i, int j) {
        if (j == word.length()) {
            return 1; // the word is spelled: strike out the rest of the text
        }
        if (i == text.length()) {
            return 0; // the text ran out before the word did
        }
        int ways = from(text, word, i + 1, j); // strike out text[i]
        if (text.charAt(i) == word.charAt(j)) {
            ways += from(text, word, i + 1, j + 1); // or keep it as word[j]
        }
        return ways;
    }
}
