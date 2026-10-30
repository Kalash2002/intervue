/** A square board of tiles in a puzzle. */
public class Board {

    /** Turns grid a quarter turn clockwise in place; grid is n x n. */
    static void rotate(int[][] grid) {
        int n = grid.length;
        for (int ring = 0; ring < n / 2; ring++) { // an odd board's centre tile stays put
            int last = n - 1 - ring;
            for (int i = 0; i < last - ring; i++) { // every tile of the top side but its last, a corner
                int top = grid[ring][ring + i];
                grid[ring][ring + i] = grid[last - i][ring]; // the left side moves up to the top
                grid[last - i][ring] = grid[last][last - i]; // the bottom moves to the left
                grid[last][last - i] = grid[ring + i][last]; // the right side moves to the bottom
                grid[ring + i][last] = top; // the top moves to the right
            }
        }
    }
}
