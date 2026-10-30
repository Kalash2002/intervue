import java.util.ArrayDeque;

/** Fills the regions of open cells that cannot reach the border of a board. */
public class Capture {

    /** Turns every 'O' whose region touches no border cell into 'X', in place. */
    static void surround(char[][] board) {
        int rows = board.length;
        int cols = board[0].length;
        boolean[][] surrounded = new boolean[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (board[r][c] == 'O') {
                    surrounded[r][c] = !reachesBorder(board, r, c); // a fresh search from every open cell
                }
            }
        }
        for (int r = 0; r < rows; r++) { // change the board only after every cell has been decided
            for (int c = 0; c < cols; c++) {
                if (surrounded[r][c]) {
                    board[r][c] = 'X';
                }
            }
        }
    }

    // Searches the open cells connected to (r, c) until one on the border turns up.
    private static boolean reachesBorder(char[][] board, int r, int c) {
        int rows = board.length;
        int cols = board[0].length;
        boolean[][] seen = new boolean[rows][cols];
        ArrayDeque<int[]> queue = new ArrayDeque<>();
        queue.add(new int[] {r, c});
        seen[r][c] = true;
        int[][] steps = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        while (!queue.isEmpty()) {
            int[] cell = queue.poll();
            if (cell[0] == 0 || cell[1] == 0 || cell[0] == rows - 1 || cell[1] == cols - 1) {
                return true;
            }
            for (int[] step : steps) {
                int nr = cell[0] + step[0];
                int nc = cell[1] + step[1];
                if (!seen[nr][nc] && board[nr][nc] == 'O') { // in range: cell is not on the border
                    seen[nr][nc] = true;
                    queue.add(new int[] {nr, nc});
                }
            }
        }
        return false;
    }
}
