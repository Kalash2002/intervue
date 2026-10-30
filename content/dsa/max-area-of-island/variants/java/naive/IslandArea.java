import java.util.ArrayDeque;

/** Measures the largest island on a text map of land and water. */
public class IslandArea {

    /** Returns the number of cells in the largest island; '#' is land, '.' is water, only side-sharing cells connect. */
    static int largest(String[] rows) {
        int best = 0;
        for (int r = 0; r < rows.length; r++) {
            for (int c = 0; c < rows[r].length(); c++) {
                if (rows[r].charAt(c) == '#') {
                    best = Math.max(best, measure(rows, r, c)); // every land cell measures its own island again
                }
            }
        }
        return best;
    }

    // Counts the cells of the island holding (r, c) with a fresh breadth-first search.
    private static int measure(String[] rows, int r, int c) {
        int width = rows[0].length();
        boolean[][] seen = new boolean[rows.length][width];
        ArrayDeque<int[]> queue = new ArrayDeque<>();
        queue.add(new int[] {r, c});
        seen[r][c] = true;
        int area = 0;
        int[][] steps = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        while (!queue.isEmpty()) {
            int[] cell = queue.poll();
            area++;
            for (int[] step : steps) {
                int nr = cell[0] + step[0];
                int nc = cell[1] + step[1];
                if (nr >= 0 && nr < rows.length && nc >= 0 && nc < width && !seen[nr][nc] && rows[nr].charAt(nc) == '#') {
                    seen[nr][nc] = true;
                    queue.add(new int[] {nr, nc});
                }
            }
        }
        return area;
    }
}
