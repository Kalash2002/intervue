/** Measures how far a typed word is from the word that was meant. */
public class Typo {

    /** Returns the fewest single-letter inserts, deletes and replacements that turn typed into word. */
    static int fewestFixes(String typed, String word) {
        return from(typed, word, 0, 0);
    }

    /** The fewest fixes turning typed from position i on into word from position j on. */
    private static int from(String typed, String word, int i, int j) {
        if (i == typed.length()) {
            return word.length() - j; // insert the rest of word
        }
        if (j == word.length()) {
            return typed.length() - i; // delete the rest of typed
        }
        if (typed.charAt(i) == word.charAt(j)) {
            return from(typed, word, i + 1, j + 1); // equal first letters: keeping them is never worse
        }
        int replace = from(typed, word, i + 1, j + 1);
        int delete = from(typed, word, i + 1, j);
        int insert = from(typed, word, i, j + 1);
        return 1 + Math.min(replace, Math.min(delete, insert));
    }
}
