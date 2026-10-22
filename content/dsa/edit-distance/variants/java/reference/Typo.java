/** Measures how far a typed word is from the word that was meant. */
public class Typo {

    /** Returns the fewest single-letter inserts, deletes and replacements that turn typed into word. */
    static int fewestFixes(String typed, String word) {
        // prev[j]: the fewest fixes turning the first i - 1 letters of typed into the first j of word
        int[] prev = new int[word.length() + 1];
        int[] cur = new int[word.length() + 1];
        for (int j = 0; j <= word.length(); j++) {
            prev[j] = j; // nothing typed: insert all j letters
        }
        for (int i = 1; i <= typed.length(); i++) {
            cur[0] = i; // nothing of word wanted: delete all i letters
            for (int j = 1; j <= word.length(); j++) {
                if (typed.charAt(i - 1) == word.charAt(j - 1)) {
                    cur[j] = prev[j - 1]; // equal last letters: keep them, no fix
                } else {
                    // replace the last typed letter, delete it, or insert the last letter of word
                    cur[j] = 1 + Math.min(prev[j - 1], Math.min(prev[j], cur[j - 1]));
                }
            }
            int[] done = prev;
            prev = cur;
            cur = done; // reused for the next row; every entry is overwritten
        }
        return prev[word.length()];
    }
}
