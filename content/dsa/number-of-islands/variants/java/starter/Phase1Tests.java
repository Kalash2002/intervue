import static kit.Check.assertEquals;

import kit.Test;

public class Phase1Tests {

    @Test(order = 1)
    void sample() {
        assertEquals(4, Archipelago.countIslands(new String[] {"##..#", "#...#", "..#..", "....#"}));
    }

    @Test(order = 2)
    void emptyMap() {
        assertEquals(0, Archipelago.countIslands(new String[0]));
    }

    @Test(order = 3)
    void allWater() {
        assertEquals(0, Archipelago.countIslands(new String[] {"...", "..."}));
    }

    @Test(order = 4)
    void allLandIsOneIsland() {
        assertEquals(1, Archipelago.countIslands(new String[] {"####", "####", "####"}));
        assertEquals(1, Archipelago.countIslands(new String[] {"#"}));
    }

    @Test(order = 5)
    void singleRow() {
        assertEquals(3, Archipelago.countIslands(new String[] {"#.#.##"}));
    }

    @Test(order = 6)
    void cornerTouchDoesNotConnect() {
        assertEquals(2, Archipelago.countIslands(new String[] {"#.", ".#"}));
    }

    @Test(order = 7)
    void ringAroundWaterAndAnIslandInsideIt() {
        assertEquals(1, Archipelago.countIslands(new String[] {"###", "#.#", "###"}));
        assertEquals(2, Archipelago.countIslands(new String[] {"#####", "#...#", "#.#.#", "#...#", "#####"}));
    }

    @Test(order = 8)
    void snakeIsOneIsland() {
        assertEquals(1, Archipelago.countIslands(new String[] {"#####", "....#", "#####", "#....", "#####"}));
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
        assertEquals(1, Archipelago.countIslands(rows));
    }

    @Test(order = 10, timeoutMs = 2000)
    void checkerboardOfSingleCells() {
        int size = 1000;
        String[] rows = new String[size];
        for (int r = 0; r < size; r++) {
            StringBuilder row = new StringBuilder(size);
            for (int c = 0; c < size; c++) {
                row.append((r + c) % 2 == 0 ? '#' : '.');
            }
            rows[r] = row.toString();
        }
        assertEquals(500_000, Archipelago.countIslands(rows));
    }
}
