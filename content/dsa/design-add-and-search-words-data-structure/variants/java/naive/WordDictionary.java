import java.util.ArrayList;
import java.util.List;

/** Stores words and answers lookups where a dot stands for any one letter. */
public class WordDictionary {

    private final List<String> words = new ArrayList<>();

    /** Stores word; storing it again changes nothing. */
    void addWord(String word) {
        words.add(word); // a repeat is harmless: search only asks whether some word fits
    }

    /** True if a stored word of the same length agrees with pattern; '.' agrees with any one letter. */
    boolean search(String pattern) {
        for (String word : words) { // compare against every stored word
            if (fits(word, pattern)) {
                return true;
            }
        }
        return false;
    }

    private static boolean fits(String word, String pattern) {
        if (word.length() != pattern.length()) {
            return false; // a dot stands for exactly one letter
        }
        for (int i = 0; i < word.length(); i++) {
            char ch = pattern.charAt(i);
            if (ch != '.' && ch != word.charAt(i)) {
                return false;
            }
        }
        return true;
    }
}
