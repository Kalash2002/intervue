/** A wall of sensors whose failed ones spoil their whole row and column. */
public class SensorGrid {

    /** Sets to 0, in place, every reading whose row or column held a 0 at the start. */
    static void clearFailed(int[][] grid) {
        int m = grid.length, n = grid[0].length;
        boolean firstColumnHasZero = false; // grid[0][0] is row 0's flag, so column 0 needs its own
        for (int r = 0; r < m; r++) {
            if (grid[r][0] == 0) {
                firstColumnHasZero = true;
            }
            for (int c = 1; c < n; c++) {
                if (grid[r][c] == 0) {
                    grid[r][0] = 0; // flag row r in column 0
                    grid[0][c] = 0; // flag column c in row 0
                }
            }
        }
        for (int r = m - 1; r >= 0; r--) { // bottom up, so row 0's flags are read before row 0 is cleared
            for (int c = 1; c < n; c++) {
                if (grid[r][0] == 0 || grid[0][c] == 0) {
                    grid[r][c] = 0;
                }
            }
            if (firstColumnHasZero) {
                grid[r][0] = 0; // after the row's other cells, which read this flag
            }
        }
    }
}
