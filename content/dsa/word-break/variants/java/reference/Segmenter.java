import java.util.List;

/** Puts the spaces back into a message typed without them. */
public class Segmenter {

    /** Returns whether text can be cut into pieces that are each one of the words. */
    static boolean canSplit(String text, List<String> words) {
        int n = text.length();
        boolean[] reachable = new boolean[n + 1]; // reachable[i]: the first i letters split into words
        reachable[0] = true;
        for (int start = 0; start < n; start++) {
            if (!reachable[start]) {
                continue; // no split ends here, so no piece can start here
            }
            for (String word : words) {
                // startsWith is false, not an exception, when the word runs past the end
                if (text.startsWith(word, start)) {
                    reachable[start + word.length()] = true;
                }
            }
        }
        return reachable[n];
    }
}
