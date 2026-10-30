/** Stores points, copies included, and counts the axis-aligned squares a query point would finish. */
public class SquareFinder {

    private static final int SIZE = 1001; // coordinates run from 0 to 1000
    private final int[][] copies = new int[SIZE][]; // copies[x][y]: how often (x, y) was added; column x made on its first point
    private final int[][] ys = new int[SIZE][]; // ys[x]: the distinct y values stored in column x
    private final int[] distinct = new int[SIZE]; // distinct[x]: how many entries of ys[x] are in use

    /** Stores the point (x, y); every copy counts. */
    void add(int x, int y) {
        if (copies[x] == null) {
            copies[x] = new int[SIZE];
            ys[x] = new int[SIZE];
        }
        if (copies[x][y]++ == 0) { // a new point joins its column's list once
            ys[x][distinct[x]++] = y;
        }
    }

    /** Returns the ways to pick three stored points that finish an axis-aligned square with (x, y). */
    long count(int x, int y) {
        long ways = 0;
        for (int i = 0; i < distinct[x]; i++) {
            int other = ys[x][i];
            int side = other - y; // a stored point above or below the query fixes the side
            if (side != 0) { // side 0 is the query's own position, not a square
                ways += copies[x][other] * (farSide(x + side, y, other) + farSide(x - side, y, other));
            }
        }
        return ways;
    }

    /** Copies of (column, y1) times copies of (column, y2): the ways to pick the square's two far corners. */
    private long farSide(int column, int y1, int y2) {
        if (column < 0 || column >= SIZE || copies[column] == null) {
            return 0;
        }
        return (long) copies[column][y1] * copies[column][y2];
    }
}
