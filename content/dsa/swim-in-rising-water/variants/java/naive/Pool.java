/** Times a swim across a basin that fills with rain. */
public class Pool {

    private static final int[][] SIDES = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

    /**
     * Returns the earliest time t at which the swimmer, starting at the top-left cell, can be in the
     * bottom-right cell, moving only through cells whose elevation is at most t.
     */
    static int earliestCrossing(int[][] elevation) {
        int n = elevation.length;
        // nothing earlier can work: both corners must be under water
        for (int t = Math.max(elevation[0][0], elevation[n - 1][n - 1]); ; t++) {
            if (canCross(elevation, t)) {
                return t;
            }
        }
    }

    /** Breadth-first search through the cells under water at time t. */
    private static boolean canCross(int[][] elevation, int t) {
        int n = elevation.length;
        boolean[][] seen = new boolean[n][n];
        int[] queue = new int[n * n]; // cells as row * n + column
        int head = 0;
        int tail = 0;
        queue[tail++] = 0;
        seen[0][0] = true;
        while (head < tail) {
            int r = queue[head] / n;
            int c = queue[head++] % n;
            if (r == n - 1 && c == n - 1) {
                return true;
            }
            for (int[] side : SIDES) {
                int nr = r + side[0];
                int nc = c + side[1];
                if (nr >= 0 && nr < n && nc >= 0 && nc < n && !seen[nr][nc] && elevation[nr][nc] <= t) {
                    seen[nr][nc] = true;
                    queue[tail++] = nr * n + nc;
                }
            }
        }
        return false;
    }
}
