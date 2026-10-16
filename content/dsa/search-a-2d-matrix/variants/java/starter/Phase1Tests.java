import static kit.Check.assertEquals;

import kit.Test;

public class Phase1Tests {

    // Built once per test JVM: 1,000 rows of 1,000 values, 3 * (flat position) - 1,500,000.
    private static final int SIDE = 1000;
    private static final int[][] BIG = new int[SIDE][SIDE];

    static {
        for (int r = 0; r < SIDE; r++) {
            for (int c = 0; c < SIDE; c++) {
                BIG[r][c] = 3 * (r * SIDE + c) - 1_500_000;
            }
        }
    }

    private static final int[][] SAMPLE = {{2, 5, 9, 14}, {20, 23, 31, 38}, {44, 50, 57, 63}};

    @Test(order = 1)
    void sample() {
        assertEquals(true, MatrixSearch.contains(SAMPLE, 31));
        assertEquals(false, MatrixSearch.contains(SAMPLE, 40));
    }

    @Test(order = 2)
    void singleCell() {
        int[][] one = {{-6}};
        assertEquals(true, MatrixSearch.contains(one, -6));
        assertEquals(false, MatrixSearch.contains(one, -7));
        assertEquals(false, MatrixSearch.contains(one, -5));
    }

    @Test(order = 3)
    void singleColumn() {
        int[][] column = {{-8}, {-3}, {6}};
        assertEquals(false, MatrixSearch.contains(column, 0));
        assertEquals(true, MatrixSearch.contains(column, -8));
        assertEquals(true, MatrixSearch.contains(column, 6));
    }

    @Test(order = 4)
    void singleRow() {
        int[][] row = {{1, 3, 7, 12, 18}};
        assertEquals(true, MatrixSearch.contains(row, 18));
        assertEquals(true, MatrixSearch.contains(row, 1));
        assertEquals(false, MatrixSearch.contains(row, 8));
    }

    @Test(order = 5)
    void corners() {
        assertEquals(true, MatrixSearch.contains(SAMPLE, 2));
        assertEquals(true, MatrixSearch.contains(SAMPLE, 14));
        assertEquals(true, MatrixSearch.contains(SAMPLE, 44));
        assertEquals(true, MatrixSearch.contains(SAMPLE, 63));
        assertEquals(false, MatrixSearch.contains(SAMPLE, 1));
        assertEquals(false, MatrixSearch.contains(SAMPLE, 64));
    }

    @Test(order = 6)
    void gapBetweenRows() {
        assertEquals(false, MatrixSearch.contains(SAMPLE, 17)); // after 14, before 20
        assertEquals(false, MatrixSearch.contains(SAMPLE, 41)); // after 38, before 44
    }

    @Test(order = 7)
    void wideAndTallGridsFindEveryValue() {
        // Two rows of five and five rows of two: mapping a flat position with the row count breaks these.
        int[][] wide = {{1, 2, 3, 4, 5}, {6, 7, 8, 9, 10}};
        int[][] tall = {{1, 2}, {3, 4}, {5, 6}, {7, 8}, {9, 10}};
        for (int v = 0; v <= 11; v++) {
            boolean expected = v >= 1 && v <= 10;
            assertEquals(expected, MatrixSearch.contains(wide, v), "wide, target " + v);
            assertEquals(expected, MatrixSearch.contains(tall, v), "tall, target " + v);
        }
    }

    @Test(order = 8)
    void negativeAndLimitValues() {
        int[][] grid = {{-1_000_000_000, -40, -3}, {0, 17, 1_000_000_000}};
        assertEquals(true, MatrixSearch.contains(grid, -1_000_000_000));
        assertEquals(true, MatrixSearch.contains(grid, 1_000_000_000));
        assertEquals(true, MatrixSearch.contains(grid, -3));
        assertEquals(false, MatrixSearch.contains(grid, -2));
    }

    @Test(order = 9, timeoutMs = 600)
    void sixtyThousandLookupsInAMillionCells() {
        for (int k = 0; k < 60_000; k++) {
            int offset = (int) ((k * 7919L) % (3L * SIDE * SIDE));
            int target = offset - 1_500_000;
            assertEquals(offset % 3 == 0, MatrixSearch.contains(BIG, target), "target " + target);
        }
    }
}
