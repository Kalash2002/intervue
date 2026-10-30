import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

/** Finds the cells of a height map whose rain can reach both oceans. */
public class WaterFlow {

    /** Returns {row, column} for every cell whose water can reach both oceans, in any order. */
    static List<int[]> bothOceans(int[][] heights) {
        List<int[]> both = new ArrayList<>();
        for (int r = 0; r < heights.length; r++) {
            for (int c = 0; c < heights[0].length; c++) {
                if (reachesBoth(heights, r, c)) { // a fresh search from every cell
                    both.add(new int[] {r, c});
                }
            }
        }
        return both;
    }

    // Follows the water downhill (to neighbours no higher) from (r, c) until both oceans are touched.
    private static boolean reachesBoth(int[][] heights, int r, int c) {
        int rows = heights.length;
        int cols = heights[0].length;
        boolean[][] seen = new boolean[rows][cols];
        ArrayDeque<int[]> queue = new ArrayDeque<>();
        queue.add(new int[] {r, c});
        seen[r][c] = true;
        boolean pacific = false;
        boolean atlantic = false;
        int[][] steps = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        while (!queue.isEmpty()) {
            int[] cell = queue.poll();
            pacific |= cell[0] == 0 || cell[1] == 0;
            atlantic |= cell[0] == rows - 1 || cell[1] == cols - 1;
            if (pacific && atlantic) {
                return true;
            }
            for (int[] step : steps) {
                int nr = cell[0] + step[0];
                int nc = cell[1] + step[1];
                if (nr >= 0 && nc >= 0 && nr < rows && nc < cols && !seen[nr][nc]
                        && heights[nr][nc] <= heights[cell[0]][cell[1]]) {
                    seen[nr][nc] = true;
                    queue.add(new int[] {nr, nc});
                }
            }
        }
        return false;
    }
}
