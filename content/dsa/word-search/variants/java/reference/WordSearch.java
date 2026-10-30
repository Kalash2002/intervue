/** Decides whether a word can be traced through side-by-side cells of a letter grid. */
public class WordSearch {

    /** Returns true if word can be spelled by steps up, down, left or right, each cell used once. */
    static boolean exists(char[][] board, String word) {
        int[] inGrid = new int[26];
        for (char[] row : board) {
            for (char ch : row) {
                inGrid[ch - 'a']++;
            }
        }
        for (int i = 0; i < word.length(); i++) {
            if (--inGrid[word.charAt(i) - 'a'] < 0) {
                return false; // the word needs more of this letter than the grid holds
            }
        }
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
            return false; // off the grid, already on this trace ('#'), or the wrong letter: stop here
        }
        if (k == word.length() - 1) {
            return true;
        }
        char letter = board[r][c];
        board[r][c] = '#'; // on the trace: no later step may come back here
        boolean found = trace(board, word, r + 1, c, k + 1)
                || trace(board, word, r - 1, c, k + 1)
                || trace(board, word, r, c + 1, k + 1)
                || trace(board, word, r, c - 1, k + 1);
        board[r][c] = letter; // off the trace again, for the other branches and the caller
        return found;
    }
}
