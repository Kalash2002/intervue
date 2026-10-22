/** Finds the longest walk on a height map that rises at every step. */
public class Hike {

    private static final int[][] STEPS = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

    /** Returns the number of cells on the longest walk that steps to side neighbours and always rises. */
    static int longestClimb(int[][] heights) {
        int best = 0;
        for (int r = 0; r < heights.length; r++) {
            for (int c = 0; c < heights[0].length; c++) {
                best = Math.max(best, climbFrom(heights, r, c)); // any cell can start the longest climb
            }
        }
        return best;
    }

    /** The number of cells on the longest climb that starts at (r, c), found by trying every climb. */
    private static int climbFrom(int[][] heights, int r, int c) {
        int best = 1; // the cell on its own
        for (int[] step : STEPS) {
            int nr = r + step[0];
            int nc = c + step[1];
            // strictly higher, so a climb can never come back to a cell and needs no visited set
            if (nr >= 0 && nc >= 0 && nr < heights.length && nc < heights[0].length && heights[nr][nc] > heights[r][c]) {
                best = Math.max(best, 1 + climbFrom(heights, nr, nc));
            }
        }
        return best;
    }
}
