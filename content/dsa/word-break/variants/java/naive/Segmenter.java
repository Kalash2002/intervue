import java.util.List;

/** Puts the spaces back into a message typed without them. */
public class Segmenter {

    /** Returns whether text can be cut into pieces that are each one of the words. */
    static boolean canSplit(String text, List<String> words) {
        return splitsFrom(text, 0, words);
    }

    private static boolean splitsFrom(String text, int start, List<String> words) {
        if (start == text.length()) {
            return true; // every letter belongs to a piece
        }
        for (String word : words) {
            // try each word as the next piece, then split the rest the same way
            if (text.startsWith(word, start) && splitsFrom(text, start + word.length(), words)) {
                return true;
            }
        }
        return false;
    }
}
