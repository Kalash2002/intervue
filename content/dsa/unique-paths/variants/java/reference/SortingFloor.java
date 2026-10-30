import java.util.Arrays;

/** Counts the routes a parcel can take across a sorting floor. */
public class SortingFloor {

    /** Returns the number of routes from the top-left cell to the bottom-right cell, moving only right or down. */
    static int routes(int rows, int cols) {
        int[] ways = new int[cols]; // ways[c]: routes into column c of the row being filled
        Arrays.fill(ways, 1); // the top row: only moves to the right reach it
        for (int r = 1; r < rows; r++) {
            for (int c = 1; c < cols; c++) {
                ways[c] += ways[c - 1]; // the old ways[c] counts routes from above, ways[c - 1] from the left
            }
        }
        return ways[cols - 1];
    }
}
