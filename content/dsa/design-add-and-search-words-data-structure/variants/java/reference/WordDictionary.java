/** Stores words and answers lookups where a dot stands for any one letter. */
public class WordDictionary {

    private static final class Node {
        final Node[] children = new Node[26]; // one slot per letter
        boolean endsWord; // true when a stored word finishes at this node
    }

    private final Node root = new Node();

    /** Stores word; storing it again changes nothing. */
    void addWord(String word) {
        Node node = root;
        for (int i = 0; i < word.length(); i++) {
            int slot = word.charAt(i) - 'a';
            if (node.children[slot] == null) {
                node.children[slot] = new Node();
            }
            node = node.children[slot];
        }
        node.endsWord = true;
    }

    /** True if a stored word of the same length agrees with pattern; '.' agrees with any one letter. */
    boolean search(String pattern) {
        return matches(root, pattern, 0);
    }

    // True if some word below node spells pattern[i..] exactly; the depth is at most 20.
    private static boolean matches(Node node, String pattern, int i) {
        if (i == pattern.length()) {
            return node.endsWord; // the pattern is used up: a word must end exactly here
        }
        char ch = pattern.charAt(i);
        if (ch != '.') {
            Node next = node.children[ch - 'a'];
            return next != null && matches(next, pattern, i + 1);
        }
        for (Node next : node.children) { // a dot: try every letter that continues some word
            if (next != null && matches(next, pattern, i + 1)) {
                return true;
            }
        }
        return false;
    }
}
