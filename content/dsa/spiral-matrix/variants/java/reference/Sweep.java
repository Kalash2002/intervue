import java.util.ArrayList;
import java.util.List;

/** Plans the path of a robot vacuum over a rectangular floor. */
public class Sweep {

    /** Returns the readings of grid in clockwise spiral order from the top-left tile; grid is not changed. */
    static List<Integer> spiralOrder(int[][] grid) {
        int m = grid.length;
        int n = grid[0].length;
        int[] dr = {0, 1, 0, -1}; // right, down, left, up
        int[] dc = {1, 0, -1, 0};
        int[] legs = {n, m - 1}; // tiles on the next horizontal and the next vertical stretch
        List<Integer> order = new ArrayList<>(m * n);
        int r = 0;
        int c = -1; // one step left of the first tile
        for (int d = 0; legs[d % 2] > 0; d = (d + 1) % 4) {
            for (int step = 0; step < legs[d % 2]; step++) {
                r += dr[d];
                c += dc[d];
                order.add(grid[r][c]);
            }
            legs[d % 2]--; // the next stretch along this axis is one tile shorter
        }
        return order;
    }
}
