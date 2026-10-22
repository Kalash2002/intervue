import java.util.ArrayList;
import java.util.List;

/** Cuts a string into the most pieces that keep every letter inside one piece. */
public class Pieces {

    /** Returns the lengths of the pieces, in order. */
    static List<Integer> sizes(String s) {
        List<Integer> sizes = new ArrayList<>();
        int start = 0;
        for (int i = 0; i < s.length(); i++) {
            if (i == s.length() - 1 || cutAllowed(s, i)) { // the end of the string always ends a piece
                sizes.add(i - start + 1);
                start = i + 1;
            }
        }
        return sizes;
    }

    /** Whether no letter appears both at or before position i and after it. */
    private static boolean cutAllowed(String s, int i) {
        boolean[] before = new boolean[26];
        for (int j = 0; j <= i; j++) {
            before[s.charAt(j) - 'a'] = true;
        }
        for (int j = i + 1; j < s.length(); j++) {
            if (before[s.charAt(j) - 'a']) {
                return false; // this letter would be in two pieces
            }
        }
        return true;
    }
}
