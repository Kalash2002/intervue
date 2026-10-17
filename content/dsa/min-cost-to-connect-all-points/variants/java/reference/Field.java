import java.util.Arrays;

/** Plans the shortest total cable that joins every sensor on a field. */
public class Field {

    /** Returns the least total cable length joining all points, cables running along the grid. */
    static int minCable(int[][] points) {
        int n = points.length;
        int[] best = new int[n];
        boolean[] joined = new boolean[n];
        Arrays.fill(best, Integer.MAX_VALUE);
        best[0] = 0;
        int total = 0;
        for (int round = 0; round < n; round++) {
            int pick = -1;
            for (int i = 0; i < n; i++) {
                if (!joined[i] && (pick == -1 || best[i] < best[pick])) {
                    pick = i;
                }
            }
            joined[pick] = true;
            total += best[pick];
            for (int i = 0; i < n; i++) {
                if (!joined[i]) {
                    int d = Math.abs(points[i][0] - points[pick][0]) + Math.abs(points[i][1] - points[pick][1]);
                    if (d < best[i]) {
                        best[i] = d;
                    }
                }
            }
        }
        return total;
    }
}
