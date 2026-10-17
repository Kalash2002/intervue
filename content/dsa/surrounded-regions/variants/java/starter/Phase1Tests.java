import static kit.Check.assertEquals;

import java.util.Arrays;
import java.util.Random;
import kit.Test;

public class Phase1Tests {

    private static char[][] board(String... rows) {
        char[][] board = new char[rows.length][];
        for (int i = 0; i < rows.length; i++) {
            board[i] = rows[i].toCharArray();
        }
        return board;
    }

    private static String text(char[][] board) {
        String[] rows = new String[board.length];
        for (int i = 0; i < board.length; i++) {
            rows[i] = new String(board[i]);
        }
        return String.join("/", rows);
    }

    private static void expect(char[][] board, char[][] want) {
        Capture.surround(board);
        assertEquals(text(want), text(board), "the board after the call, rows separated by /");
    }

    @Test(order = 1)
    void sample() {
        expect(board("XXXXX", "XOOXO", "XXOXO", "XOXXX"), board("XXXXX", "XXXXO", "XXXXO", "XOXXX"));
    }

    @Test(order = 2)
    void aCornerTouchDoesNotConnect() {
        expect(board("XXX", "XOX", "XXO"), board("XXX", "XXX", "XXO"));
    }

    @Test(order = 3)
    void singleCellAndSingleRow() {
        expect(board("O"), board("O"));
        expect(board("OXO"), board("OXO"));
        expect(board("X", "O", "X"), board("X", "O", "X"));
    }

    @Test(order = 4)
    void allOpenAndAllWalled() {
        expect(board("OOO", "OOO", "OOO"), board("OOO", "OOO", "OOO"));
        expect(board("XXXX", "XXXX"), board("XXXX", "XXXX"));
    }

    @Test(order = 5)
    void oneOpeningSavesTheWholeRegion() {
        expect(board("XXXXX", "XOOOX", "XOOOX", "XXXOX"), board("XXXXX", "XOOOX", "XOOOX", "XXXOX"));
    }

    @Test(order = 6)
    void ringsInsideRings() {
        expect(board("XXXXXXX", "XOOOOOX", "XOXXXOX", "XOXOXOX", "XOXXXOX", "XOOOOOX", "XXXXXXX"),
                board("XXXXXXX", "XXXXXXX", "XXXXXXX", "XXXXXXX", "XXXXXXX", "XXXXXXX", "XXXXXXX"));
        expect(board("XXXXXXX", "XOOOOOO", "XOXXXOX", "XOXOXOX", "XOXXXOX", "XOOOOOX", "XXXXXXX"),
                board("XXXXXXX", "XOOOOOO", "XOXXXOX", "XOXXXOX", "XOXXXOX", "XOOOOOX", "XXXXXXX"));
    }

    @Test(order = 7)
    void aLongPathToTheBorder() {
        // the region winds a long way before it touches the border, only at the end of row 5
        expect(board("XXXXXX", "XOOOOX", "XXXXOX", "XOOOOX", "XOXXXX", "XOOOOO", "XXXXXX"),
                board("XXXXXX", "XOOOOX", "XXXXOX", "XOOOOX", "XOXXXX", "XOOOOO", "XXXXXX"));
    }

    @Test(order = 8)
    void randomSmallBoardsAgainstOneSearchPerCell() {
        Random random = new Random(84);
        for (int round = 0; round < 30; round++) {
            char[][] board = new char[1 + random.nextInt(9)][1 + random.nextInt(9)];
            for (char[] row : board) {
                for (int c = 0; c < row.length; c++) {
                    row[c] = random.nextInt(3) == 0 ? 'X' : 'O';
                }
            }
            char[][] want = new char[board.length][];
            for (int r = 0; r < board.length; r++) {
                want[r] = board[r].clone();
                for (int c = 0; c < board[r].length; c++) {
                    if (board[r][c] == 'O' && !reachesBorder(board, r, c, new boolean[board.length][board[0].length])) {
                        want[r][c] = 'X';
                    }
                }
            }
            expect(board, want);
        }
    }

    @Test(order = 9, timeoutMs = 1000)
    void oneHugeEnclosedRegion() {
        int n = 200;
        char[][] board = new char[n][n];
        for (char[] row : board) {
            Arrays.fill(row, 'O');
        }
        for (int i = 0; i < n; i++) { // a wall all round the border
            board[0][i] = 'X';
            board[n - 1][i] = 'X';
            board[i][0] = 'X';
            board[i][n - 1] = 'X';
        }
        char[][] want = new char[n][n];
        for (char[] row : want) {
            Arrays.fill(row, 'X');
        }
        expect(board, want);
    }

    @Test(order = 10)
    void allOpenLargeBoard() {
        int n = 200;
        char[][] board = new char[n][n];
        char[][] want = new char[n][n];
        for (int r = 0; r < n; r++) {
            Arrays.fill(board[r], 'O'); // a search from the border can wind 40,000 cells deep here
            Arrays.fill(want[r], 'O');
        }
        expect(board, want);
    }

    // Whether the open cell (r, c) connects to an open cell on the border; used for the random test.
    private static boolean reachesBorder(char[][] board, int r, int c, boolean[][] seen) {
        if (r < 0 || c < 0 || r >= board.length || c >= board[0].length || seen[r][c] || board[r][c] != 'O') {
            return false;
        }
        if (r == 0 || c == 0 || r == board.length - 1 || c == board[0].length - 1) {
            return true;
        }
        seen[r][c] = true;
        return reachesBorder(board, r + 1, c, seen) || reachesBorder(board, r - 1, c, seen)
                || reachesBorder(board, r, c + 1, seen) || reachesBorder(board, r, c - 1, seen);
    }
}
