import static kit.Check.assertEquals;

import kit.Test;

public class Phase1Tests {

    /** A board of 9 rows of 9 dots, to which put() adds digits. */
    private static char[][] empty() {
        char[][] g = new char[9][9];
        for (char[] row : g) {
            java.util.Arrays.fill(row, '.');
        }
        return g;
    }

    private static String[] rows(char[][] g) {
        String[] rows = new String[9];
        for (int r = 0; r < 9; r++) {
            rows[r] = new String(g[r]);
        }
        return rows;
    }

    /** A fully solved board: cell (r, c) holds (3r + r/3 + c) mod 9 + 1. */
    private static char[][] solved() {
        char[][] g = empty();
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                g[r][c] = (char) ('0' + (3 * r + r / 3 + c) % 9 + 1);
            }
        }
        return g;
    }

    @Test(order = 1)
    void sample() {
        String[] board = {
            "1..4..7..",
            "..6..9..3",
            ".8..2..5.",
            "2..5..8..",
            "..7..1..4",
            ".9..3..6.",
            "3..6..9..",
            "..8..2..5",
            ".1..4..7."
        };
        assertEquals(true, Sudoku.isValid(board));
        board[0] = "1..4..7.1";
        assertEquals(false, Sudoku.isValid(board));
    }

    @Test(order = 2)
    void solvedBoard() {
        assertEquals(true, Sudoku.isValid(rows(solved())));
    }

    @Test(order = 3)
    void emptyBoard() {
        assertEquals(true, Sudoku.isValid(rows(empty())));
    }

    @Test(order = 4)
    void repeatInARow() {
        char[][] g = empty();
        g[0][0] = '5';
        g[0][8] = '5';
        assertEquals(false, Sudoku.isValid(rows(g)));
    }

    @Test(order = 5)
    void repeatInAColumn() {
        char[][] g = empty();
        g[0][0] = '5';
        g[8][0] = '5';
        assertEquals(false, Sudoku.isValid(rows(g)));
    }

    @Test(order = 6)
    void repeatOnlyInTheTopLeftBox() {
        char[][] g = empty();
        g[0][0] = '5';
        g[1][1] = '5';
        assertEquals(false, Sudoku.isValid(rows(g)));
    }

    @Test(order = 7)
    void repeatOnlyInOtherBoxes() {
        char[][] centre = empty();
        centre[3][3] = '8';
        centre[5][5] = '8';
        assertEquals(false, Sudoku.isValid(rows(centre)));
        char[][] corner = empty();
        corner[6][6] = '2';
        corner[8][8] = '2';
        assertEquals(false, Sudoku.isValid(rows(corner)));
        char[][] lastRow = empty();
        lastRow[8][2] = '9';
        lastRow[8][6] = '9';
        assertEquals(false, Sudoku.isValid(rows(lastRow)));
    }

    @Test(order = 8)
    void sameDigitInNineDifferentBoxes() {
        char[][] g = empty();
        int[] cols = {0, 3, 6, 1, 4, 7, 2, 5, 8}; // one per row, one per column, one per box
        for (int r = 0; r < 9; r++) {
            g[r][cols[r]] = '5';
        }
        assertEquals(true, Sudoku.isValid(rows(g)));
    }

    @Test(order = 9)
    void unsolvableButWithoutRepeats() {
        char[][] g = empty();
        g[0] = "12345678.".toCharArray();
        g[1][8] = '9'; // row 0 now needs a 9 where column 8 already has one
        assertEquals(true, Sudoku.isValid(rows(g)));
    }
}
