import static kit.Check.assertEquals;

import kit.Test;

public class Phase1Tests {

    @Test(order = 1)
    void sample() {
        assertEquals(4, IslandArea.largest(new String[] {"##..#", ".#..#", "...##", "#...."}));
    }

    @Test(order = 2)
    void emptyMap() {
        assertEquals(0, IslandArea.largest(new String[0]));
    }

    @Test(order = 3)
    void allWater() {
        assertEquals(0, IslandArea.largest(new String[] {"...", "..."}));
    }

    @Test(order = 4)
    void allLandAndASingleCell() {
        assertEquals(12, IslandArea.largest(new String[] {"####", "####", "####"}));
        assertEquals(1, IslandArea.largest(new String[] {"#"}));
    }

    @Test(order = 5)
    void cornerTouchDoesNotConnect() {
        assertEquals(1, IslandArea.largest(new String[] {"#.#", ".#.", "#.#"}));
    }

    @Test(order = 6)
    void largestIslandFirstOrLast() {
        // a version that keeps the first island's area, or the last one's, gets these wrong
        assertEquals(4, IslandArea.largest(new String[] {"#...", "..##", "..##"}));
        assertEquals(3, IslandArea.largest(new String[] {"##.#", "#..."}));
    }

    @Test(order = 7)
    void ringsCountTheirCellsNotTheWaterInside() {
        assertEquals(10, IslandArea.largest(new String[] {"####", "#..#", "####"}));
        assertEquals(16, IslandArea.largest(new String[] {"#####", "#...#", "#.#.#", "#...#", "#####"}));
    }

    @Test(order = 8)
    void snakeIsOneIsland() {
        assertEquals(17, IslandArea.largest(new String[] {"#####", "....#", "#####", "#....", "#####"}));
    }

    @Test(order = 9, timeoutMs = 2000)
    void serpentineIsland() {
        int size = 1000;
        String[] rows = new String[size];
        for (int r = 0; r < size; r++) {
            if (r % 2 == 0) {
                rows[r] = "#".repeat(size);
            } else if (r % 4 == 1) {
                rows[r] = ".".repeat(size - 1) + "#"; // joins the rows above and below on the right
            } else {
                rows[r] = "#" + ".".repeat(size - 1); // joins them on the left
            }
        }
        assertEquals(500 * 1000 + 500, IslandArea.largest(rows));
    }

    @Test(order = 10, timeoutMs = 2000)
    void squaresOfDifferentSizes() {
        // blocks of side 1 to 9 down the rows, one water line between them; the 9 by 9 blocks win
        int size = 1000;
        StringBuilder[] grid = new StringBuilder[size];
        for (int r = 0; r < size; r++) {
            grid[r] = new StringBuilder(".".repeat(size));
        }
        int r = 0;
        for (int side = 1; r + side <= size; side = side % 9 + 1) {
            for (int c = 0; c + side <= size; c += side + 1) {
                for (int dr = 0; dr < side; dr++) {
                    for (int dc = 0; dc < side; dc++) {
                        grid[r + dr].setCharAt(c + dc, '#');
                    }
                }
            }
            r += side + 1;
        }
        String[] rows = new String[size];
        for (int i = 0; i < size; i++) {
            rows[i] = grid[i].toString();
        }
        assertEquals(81, IslandArea.largest(rows));
    }
}
