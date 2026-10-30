/** Counts the routes a parcel can take across a sorting floor. */
public class SortingFloor {

    /** Returns the number of routes from the top-left cell to the bottom-right cell, moving only right or down. */
    static int routes(int rows, int cols) {
        return from(0, 0, rows, cols);
    }

    /** Routes from cell (r, c) to the bottom-right cell. */
    private static int from(int r, int c, int rows, int cols) {
        if (r == rows - 1 || c == cols - 1) {
            return 1; // on the last row or column one straight run is left
        }
        return from(r + 1, c, rows, cols) + from(r, c + 1, rows, cols); // move down first, or right first
    }
}
