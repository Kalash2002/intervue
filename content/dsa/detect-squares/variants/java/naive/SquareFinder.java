import java.util.ArrayList;
import java.util.List;

/** Stores points, copies included, and counts the axis-aligned squares a query point would finish. */
public class SquareFinder {

    private final List<int[]> points = new ArrayList<>(); // every added point, copies included

    /** Stores the point (x, y); every copy counts. */
    void add(int x, int y) {
        points.add(new int[] {x, y});
    }

    /** Returns the ways to pick three stored points that finish an axis-aligned square with (x, y). */
    long count(int x, int y) {
        long ways = 0;
        for (int[] far : points) { // each copy of the corner diagonally opposite the query
            int dx = far[0] - x;
            int dy = far[1] - y;
            if (dx != 0 && Math.abs(dx) == Math.abs(dy)) { // a square, not a line or a rectangle
                ways += copiesOf(far[0], y) * copiesOf(x, far[1]); // the two corners left to choose
            }
        }
        return ways;
    }

    private long copiesOf(int x, int y) {
        long copies = 0;
        for (int[] p : points) {
            if (p[0] == x && p[1] == y) {
                copies++;
            }
        }
        return copies;
    }
}
