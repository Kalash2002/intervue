import java.util.HashMap;
import java.util.Map;

/** Looks for rearrangements of a word inside a text. */
public class Scramble {

    private static Map<Character, Integer> counts(String s, int from, int to) {
        Map<Character, Integer> counts = new HashMap<>();
        for (int i = from; i < to; i++) {
            counts.merge(s.charAt(i), 1, Integer::sum);
        }
        return counts;
    }

    /** Returns true if some stretch of the text is a rearrangement of the word. */
    static boolean appearsIn(String word, String text) {
        int m = word.length();
        Map<Character, Integer> need = counts(word, 0, m);
        for (int start = 0; start + m <= text.length(); start++) {
            if (need.equals(counts(text, start, start + m))) {
                return true;
            }
        }
        return false;
    }
}
