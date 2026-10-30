import java.util.ArrayList;
import java.util.List;

/** Finds the words of a list that can be traced through side-by-side cells of a letter grid. */
public class WordHunt {

    private static final class Node {
        final Node[] children = new Node[26];
        String word; // the word that ends here, until it is found
    }

    /** Returns each word of words that can be traced in board, once, in any order. */
    static List<String> findAll(char[][] board, String[] words) {
        Node root = new Node();
        for (String word : words) {
            Node node = root;
            for (int i = 0; i < word.length(); i++) {
                int slot = word.charAt(i) - 'a';
                if (node.children[slot] == null) {
                    node.children[slot] = new Node();
                }
                node = node.children[slot];
            }
            node.word = word;
        }
        List<String> found = new ArrayList<>();
        for (int r = 0; r < board.length; r++) {
            for (int c = 0; c < board[0].length; c++) {
                trace(board, r, c, root, found);
            }
        }
        return found;
    }

    // Walks the grid and the trie together from cell (r, c); node is the trie node of the letters so far.
    private static void trace(char[][] board, int r, int c, Node node, List<String> found) {
        if (r < 0 || c < 0 || r >= board.length || c >= board[0].length || board[r][c] == '#') {
            return; // off the grid or already on this trace
        }
        Node next = node.children[board[r][c] - 'a'];
        if (next == null) {
            return; // no word continues with this letter: the whole branch is dead
        }
        if (next.word != null) {
            found.add(next.word);
            next.word = null; // report each word once, however many traces spell it
        }
        char letter = board[r][c];
        board[r][c] = '#';
        trace(board, r + 1, c, next, found);
        trace(board, r - 1, c, next, found);
        trace(board, r, c + 1, next, found);
        trace(board, r, c - 1, next, found);
        board[r][c] = letter; // restore for the other branches and the caller
    }
}
