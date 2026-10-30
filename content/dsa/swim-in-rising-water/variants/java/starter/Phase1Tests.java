import static kit.Check.assertEquals;

import java.util.Arrays;
import java.util.Random;
import java.util.TreeSet;
import kit.Test;

public class Phase1Tests {

    @Test(order = 1)
    void sample() {
        assertEquals(7, Pool.earliestCrossing(new int[][] {{1, 8, 3}, {6, 9, 2}, {7, 5, 4}}));
        assertEquals(3, Pool.earliestCrossing(new int[][] {{3, 3, 9}, {9, 3, 9}, {9, 3, 3}}));
        assertEquals(6, Pool.earliestCrossing(new int[][] {{6}}));
    }

    @Test(order = 2)
    void theCornersMustBeUnderWater() {
        assertEquals(9, Pool.earliestCrossing(new int[][] {{9, 1}, {2, 3}}), "the start is the highest cell");
        assertEquals(8, Pool.earliestCrossing(new int[][] {{0, 1}, {2, 8}}), "the goal is the highest cell");
    }

    @Test(order = 3)
    void flatFloors() {
        assertEquals(0, Pool.earliestCrossing(new int[][] {{0}}));
        assertEquals(5, Pool.earliestCrossing(new int[][] {{5, 5}, {5, 5}}));
    }

    @Test(order = 4)
    void theRouteMayGoLeftAndUp() {
        // the only low route snakes right, down, left, down, right; moving only right and down meets a 30
        int[][] elevation = {
            {0, 1, 2, 3, 4},
            {30, 30, 30, 30, 5},
            {10, 9, 8, 7, 6},
            {11, 30, 30, 30, 30},
            {12, 13, 14, 15, 16}
        };
        assertEquals(16, Pool.earliestCrossing(elevation));
    }

    @Test(order = 5)
    void aLowValleyBehindAHighRidge() {
        // the lowest neighbour of the start (2) leads into a pocket walled in by 50s; the way out is the 9
        int[][] elevation = {
            {0, 2, 2, 50},
            {9, 50, 2, 50},
            {9, 50, 50, 50},
            {9, 9, 9, 1}
        };
        assertEquals(9, Pool.earliestCrossing(elevation));
    }

    @Test(order = 6)
    void randomSmallBasins() {
        Random random = new Random(96);
        for (int round = 0; round < 300; round++) {
            int n = 1 + random.nextInt(6);
            int[][] elevation = new int[n][n];
            for (int[] row : elevation) {
                for (int c = 0; c < n; c++) {
                    row[c] = random.nextInt(21); // small range, so heights repeat
                }
            }
            assertEquals(byEveryLevel(elevation), Pool.earliestCrossing(elevation), Arrays.deepToString(elevation));
        }
    }

    /** Tries each elevation in the grid as the time, lowest first, with a flood fill. */
    private static int byEveryLevel(int[][] elevation) {
        TreeSet<Integer> levels = new TreeSet<>();
        for (int[] row : elevation) {
            for (int h : row) {
                levels.add(h);
            }
        }
        for (int t : levels) {
            boolean[][] wet = new boolean[elevation.length][elevation.length];
            fill(elevation, 0, 0, t, wet);
            if (wet[elevation.length - 1][elevation.length - 1]) {
                return t;
            }
        }
        throw new AssertionError("the highest level always connects the corners");
    }

    private static void fill(int[][] elevation, int r, int c, int t, boolean[][] wet) {
        int n = elevation.length;
        if (r < 0 || r >= n || c < 0 || c >= n || wet[r][c] || elevation[r][c] > t) {
            return;
        }
        wet[r][c] = true;
        fill(elevation, r + 1, c, t, wet);
        fill(elevation, r - 1, c, t, wet);
        fill(elevation, r, c + 1, t, wet);
        fill(elevation, r, c - 1, t, wet);
    }

    @Test(order = 7, timeoutMs = 1000)
    void aLongWindingChannel() {
        // a channel snakes through walls of 10^6, every row left to right then the next right to left;
        // its floor rises by 20 per cell, except the goal, which is at 0
        int n = 297;
        int[][] elevation = new int[n][n];
        for (int[] row : elevation) {
            Arrays.fill(row, 1_000_000);
        }
        int length = 0;
        int last = 0;
        for (int r = 0; r < n; r += 2) {
            boolean rightwards = r % 4 == 0;
            for (int i = 0; i < n; i++) {
                int c = rightwards ? i : n - 1 - i;
                last = 20 * length++;
                elevation[r][c] = last;
            }
            if (r + 1 < n) {
                last = 20 * length++;
                elevation[r + 1][rightwards ? n - 1 : 0] = last; // the gap down to the next row
            }
        }
        elevation[n - 1][n - 1] = 0;
        int highest = last - 20; // the cell before the goal
        assertEquals(highest, Pool.earliestCrossing(elevation));
    }

    @Test(order = 8, timeoutMs = 1000)
    void aWallWithOneGap() {
        // low random floor on both sides of a high wall across the middle; the wall's one gap is at 550,000
        int n = 300;
        Random random = new Random(300);
        int[][] elevation = new int[n][n];
        for (int r = 0; r < n; r++) {
            for (int c = 0; c < n; c++) {
                elevation[r][c] = r == 150 ? 600_000 + random.nextInt(400_001) : random.nextInt(500_001);
            }
        }
        elevation[150][217] = 550_000;
        elevation[0][0] = 0;
        elevation[n - 1][n - 1] = 0;
        assertEquals(550_000, Pool.earliestCrossing(elevation));
    }
}
