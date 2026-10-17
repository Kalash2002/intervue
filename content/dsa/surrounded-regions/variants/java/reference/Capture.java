/** Fills the regions of open cells that cannot reach the border of a board. */
public class Capture {

    /** Turns every 'O' whose region touches no border cell into 'X', in place. */
    static void surround(char[][] board) {
        int rows = board.length;
        int cols = board[0].length;
        int[] stack = new int[rows * cols];
        int top = 0;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if ((r == 0 || c == 0 || r == rows - 1 || c == cols - 1) && board[r][c] == 'O') {
                    board[r][c] = 'S'; // safe: on the border
                    stack[top++] = r * cols + c;
                }
            }
        }
        while (top > 0) { // spread "safe" from the border to every open cell connected to it
            int cell = stack[--top];
            int r = cell / cols;
            int c = cell % cols;
            if (r > 0) top = mark(board, stack, top, r - 1, c);
            if (r + 1 < rows) top = mark(board, stack, top, r + 1, c);
            if (c > 0) top = mark(board, stack, top, r, c - 1);
            if (c + 1 < cols) top = mark(board, stack, top, r, c + 1);
        }
        for (char[] row : board) {
            for (int c = 0; c < cols; c++) {
                row[c] = row[c] == 'S' ? 'O' : 'X'; // open and unmarked means surrounded
            }
        }
    }

    /** Marks an open cell safe and pushes it; returns the new stack size. */
    private static int mark(char[][] board, int[] stack, int top, int r, int c) {
        if (board[r][c] == 'O') {
            board[r][c] = 'S'; // marked on push, so no cell is pushed twice
            stack[top++] = r * board[0].length + c;
        }
        return top;
    }
}
