/** Plans the shortest total cable that joins every sensor on a field. */
public class Field {

    /** Returns the least total cable length joining all points, cables running along the grid. */
    static int minCable(int[][] points) {
        int n = points.length;
        boolean[] joined = new boolean[n];
        joined[0] = true;
        int total = 0;
        for (int added = 1; added < n; added++) {
            int bestCost = Integer.MAX_VALUE;
            int bestPoint = -1;
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (joined[i] && !joined[j]) {
                        int d = Math.abs(points[i][0] - points[j][0]) + Math.abs(points[i][1] - points[j][1]);
                        if (d < bestCost) {
                            bestCost = d;
                            bestPoint = j;
                        }
                    }
                }
            }
            joined[bestPoint] = true;
            total += bestCost;
        }
        return total;
    }
}
