import java.util.PriorityQueue;

/** Times a swim across a basin that fills with rain. */
public class Pool {

    private static final int[][] SIDES = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

    /**
     * Returns the earliest time t at which the swimmer, starting at the top-left cell, can be in the
     * bottom-right cell, moving only through cells whose elevation is at most t.
     */
    static int earliestCrossing(int[][] elevation) {
        int n = elevation.length;
        boolean[][] queued = new boolean[n][n];
        // entries are {time the cell becomes reachable, row, column}, earliest first
        PriorityQueue<int[]> frontier = new PriorityQueue<>((a, b) -> Integer.compare(a[0], b[0]));
        frontier.add(new int[] {elevation[0][0], 0, 0});
        queued[0][0] = true;
        while (true) {
            int[] cell = frontier.poll(); // no cell outside the region is reachable sooner
            if (cell[1] == n - 1 && cell[2] == n - 1) {
                return cell[0];
            }
            for (int[] side : SIDES) {
                int r = cell[1] + side[0];
                int c = cell[2] + side[1];
                // queuing once is enough: times leave the queue in order, so a later offer is never earlier
                if (r >= 0 && r < n && c >= 0 && c < n && !queued[r][c]) {
                    queued[r][c] = true;
                    frontier.add(new int[] {Math.max(cell[0], elevation[r][c]), r, c});
                }
            }
        }
    }
}
