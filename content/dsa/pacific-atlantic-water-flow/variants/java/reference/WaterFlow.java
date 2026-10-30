import java.util.ArrayList;
import java.util.List;

/** Finds the cells of a height map whose rain can reach both oceans. */
public class WaterFlow {

    /** Returns {row, column} for every cell whose water can reach both oceans, in any order. */
    static List<int[]> bothOceans(int[][] heights) {
        int rows = heights.length;
        int cols = heights[0].length;
        boolean[] pacific = new boolean[rows * cols];
        boolean[] atlantic = new boolean[rows * cols];
        int[] pQueue = new int[rows * cols];
        int[] aQueue = new int[rows * cols];
        int pTail = 0;
        int aTail = 0;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                int cell = r * cols + c;
                if (r == 0 || c == 0) { // top and left edges touch the Pacific
                    pacific[cell] = true;
                    pQueue[pTail++] = cell;
                }
                if (r == rows - 1 || c == cols - 1) { // bottom and right edges touch the Atlantic
                    atlantic[cell] = true;
                    aQueue[aTail++] = cell;
                }
            }
        }
        climb(heights, pacific, pQueue, pTail);
        climb(heights, atlantic, aQueue, aTail);
        List<int[]> both = new ArrayList<>();
        for (int cell = 0; cell < rows * cols; cell++) {
            if (pacific[cell] && atlantic[cell]) {
                both.add(new int[] {cell / cols, cell % cols});
            }
        }
        return both;
    }

    // Breadth-first search from the queued border cells, moving only to neighbours at least as high:
    // water from such a neighbour can flow down to the cell we came from, and on to the ocean.
    private static void climb(int[][] heights, boolean[] reaches, int[] queue, int tail) {
        int cols = heights[0].length;
        int[][] steps = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        for (int head = 0; head < tail; head++) {
            int r = queue[head] / cols;
            int c = queue[head] % cols;
            for (int[] step : steps) {
                int nr = r + step[0];
                int nc = c + step[1];
                if (nr < 0 || nc < 0 || nr >= heights.length || nc >= cols) {
                    continue;
                }
                int next = nr * cols + nc;
                if (!reaches[next] && heights[nr][nc] >= heights[r][c]) {
                    reaches[next] = true; // mark when queued, so no cell is queued twice
                    queue[tail++] = next;
                }
            }
        }
    }
}
