import java.util.ArrayList;
import java.util.List;

/** Stores words and answers whole-word and prefix lookups. */
public class Trie {

    private final List<String> words = new ArrayList<>();

    /** Adds word; adding it again changes nothing. */
    void insert(String word) {
        if (!search(word)) {
            words.add(word);
        }
    }

    /** True only if exactly this word was inserted. */
    boolean search(String word) {
        for (String stored : words) { // look at every stored word
            if (stored.equals(word)) {
                return true;
            }
        }
        return false;
    }

    /** True if some inserted word begins with prefix. */
    boolean startsWith(String prefix) {
        for (String stored : words) {
            if (stored.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }
}
