import java.util.Arrays;

/** A wall of sensors whose failed ones spoil their whole row and column. */
public class SensorGrid {

    /** Sets to 0, in place, every reading whose row or column held a 0 at the start. */
    static void clearFailed(int[][] grid) {
        int m = grid.length, n = grid[0].length;
        int[][] start = new int[m][];
        for (int r = 0; r < m; r++) {
            start[r] = grid[r].clone(); // the readings before any clearing
        }
        for (int r = 0; r < m; r++) {
            for (int c = 0; c < n; c++) {
                if (start[r][c] == 0) { // a failed sensor in the copy, never a reading cleared here
                    Arrays.fill(grid[r], 0);
                    for (int i = 0; i < m; i++) {
                        grid[i][c] = 0;
                    }
                }
            }
        }
    }
}
