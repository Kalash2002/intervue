/** Decides whether a word can be traced through side-by-side cells of a letter grid. */
public class WordSearch {

    /** Returns true if word can be spelled by steps up, down, left or right, each cell used once. */
    static boolean exists(char[][] board, String word) {
        boolean[][] used = new boolean[board.length][board[0].length];
        char[] path = new char[word.length()];
        for (int r = 0; r < board.length; r++) {
            for (int c = 0; c < board[0].length; c++) {
                if (walk(board, word, used, path, r, c, 0)) {
                    return true;
                }
            }
        }
        return false;
    }

    // Walks every path of word.length() cells from (r, c) and compares only when the path is complete.
    private static boolean walk(char[][] board, String word, boolean[][] used, char[] path, int r, int c, int k) {
        if (r < 0 || c < 0 || r >= board.length || c >= board[0].length || used[r][c]) {
            return false;
        }
        path[k] = board[r][c];
        if (k == word.length() - 1) {
            return new String(path).equals(word);
        }
        used[r][c] = true;
        boolean found = walk(board, word, used, path, r + 1, c, k + 1)
                || walk(board, word, used, path, r - 1, c, k + 1)
                || walk(board, word, used, path, r, c + 1, k + 1)
                || walk(board, word, used, path, r, c - 1, k + 1);
        used[r][c] = false;
        return found;
    }
}
