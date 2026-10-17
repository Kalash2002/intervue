import java.util.ArrayList;
import java.util.List;

/** Finds the words of a list that can be traced through side-by-side cells of a letter grid. */
public class WordHunt {

    /** Returns each word of words that can be traced in board, once, in any order. */
    static List<String> findAll(char[][] board, String[] words) {
        List<String> found = new ArrayList<>();
        for (String word : words) { // one full grid search per word
            if (exists(board, word)) {
                found.add(word);
            }
        }
        return found;
    }

    private static boolean exists(char[][] board, String word) {
        for (int r = 0; r < board.length; r++) {
            for (int c = 0; c < board[0].length; c++) {
                if (trace(board, word, r, c, 0)) {
                    return true;
                }
            }
        }
        return false;
    }

    // True if word[k..] can be traced starting on cell (r, c).
    private static boolean trace(char[][] board, String word, int r, int c, int k) {
        if (r < 0 || c < 0 || r >= board.length || c >= board[0].length || board[r][c] != word.charAt(k)) {
            return false;
        }
        if (k == word.length() - 1) {
            return true;
        }
        char letter = board[r][c];
        board[r][c] = '#';
        boolean found = trace(board, word, r + 1, c, k + 1)
                || trace(board, word, r - 1, c, k + 1)
                || trace(board, word, r, c + 1, k + 1)
                || trace(board, word, r, c - 1, k + 1);
        board[r][c] = letter;
        return found;
    }
}
