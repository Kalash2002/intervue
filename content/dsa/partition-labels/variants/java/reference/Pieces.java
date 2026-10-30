import java.util.ArrayList;
import java.util.List;

/** Cuts a string into the most pieces that keep every letter inside one piece. */
public class Pieces {

    /** Returns the lengths of the pieces, in order. */
    static List<Integer> sizes(String s) {
        int[] last = new int[26]; // the last position of each letter
        for (int i = 0; i < s.length(); i++) {
            last[s.charAt(i) - 'a'] = i;
        }
        List<Integer> sizes = new ArrayList<>();
        int start = 0;
        int end = 0; // the piece must reach the last copy of every letter it holds so far
        for (int i = 0; i < s.length(); i++) {
            end = Math.max(end, last[s.charAt(i) - 'a']);
            if (i == end) { // no letter of this piece appears after i
                sizes.add(end - start + 1);
                start = i + 1;
            }
        }
        return sizes;
    }
}
