/** Stores words and answers whole-word and prefix lookups. */
public class Trie {

    private static final class Node {
        final Node[] children = new Node[26]; // one slot per letter
        boolean endsWord; // true when a word finishes at this node
    }

    private final Node root = new Node();

    /** Adds word; adding it again changes nothing. */
    void insert(String word) {
        Node node = root;
        for (int i = 0; i < word.length(); i++) {
            int slot = word.charAt(i) - 'a';
            if (node.children[slot] == null) {
                node.children[slot] = new Node(); // create the path as we go
            }
            node = node.children[slot];
        }
        node.endsWord = true;
    }

    /** True only if exactly this word was inserted. */
    boolean search(String word) {
        Node node = walk(word);
        return node != null && node.endsWord; // reaching the node is not enough
    }

    /** True if some inserted word begins with prefix. */
    boolean startsWith(String prefix) {
        return walk(prefix) != null;
    }

    /** The node reached by spelling text from the root, or null when the path breaks. */
    private Node walk(String text) {
        Node node = root;
        for (int i = 0; i < text.length() && node != null; i++) {
            node = node.children[text.charAt(i) - 'a'];
        }
        return node;
    }
}
