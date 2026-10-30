import static kit.Check.assertEquals;
import static kit.Check.assertTrue;

import java.util.Arrays;
import java.util.Random;
import kit.Test;

public class Phase1Tests {

    private static final int MIN = Integer.MIN_VALUE;
    private static final int MAX = Integer.MAX_VALUE;

    /** Clears grid with the method under test and checks that grid itself now holds want. */
    private static void expectCleared(int[][] want, int[][] grid) {
        int m = grid.length;
        int n = grid[0].length;
        String input = m * n <= 40 ? "grid = " + Arrays.deepToString(grid) : m + " x " + n + " grid";
        SensorGrid.clearFailed(grid); // grid still names the caller's array, so only in-place changes show here
        assertEquals(m, grid.length, "the number of rows must not change for " + input);
        for (int r = 0; r < m; r++) {
            assertTrue(grid[r] != null && grid[r].length == n, "row " + r + " must still hold " + n + " readings for " + input);
            for (int c = 0; c < n; c++) {
                if (grid[r][c] != want[r][c]) { // the message is built only for a wrong reading
                    assertEquals(want[r][c], grid[r][c], "the array passed in, row " + r + ", column " + c + ", for " + input);
                }
            }
        }
    }

    /** The cleared grid, built in a separate array: a reading stays when its row and its column held no 0. */
    private static int[][] cleared(int[][] grid) {
        int m = grid.length;
        int n = grid[0].length;
        boolean[] rowFailed = new boolean[m];
        boolean[] columnFailed = new boolean[n];
        for (int r = 0; r < m; r++) {
            for (int c = 0; c < n; c++) {
                if (grid[r][c] == 0) {
                    rowFailed[r] = true;
                    columnFailed[c] = true;
                }
            }
        }
        int[][] out = new int[m][n];
        for (int r = 0; r < m; r++) {
            for (int c = 0; c < n; c++) {
                out[r][c] = rowFailed[r] || columnFailed[c] ? 0 : grid[r][c];
            }
        }
        return out;
    }

    @Test(order = 1)
    void sample() {
        expectCleared(
                new int[][] {{7, 0, 5, 0, 3}, {0, 0, 0, 0, 0}, {0, 0, 0, 0, 0}, {13, 0, 15, 0, 5}},
                new int[][] {{7, 12, 5, 9, 3}, {4, 0, 8, 6, 11}, {2, 14, 10, 0, 1}, {13, 6, 15, 8, 5}});
        expectCleared(new int[][] {{0, 0, 0, 0, 0}, {8, 0, 1, 9, 7}}, new int[][] {{3, 0, 6, 2, 5}, {8, 4, 1, 9, 7}});
        expectCleared(
                new int[][] {{0, 0}, {0, -1}, {0, MIN}, {0, MAX}},
                new int[][] {{0, 4}, {5, -1}, {6, MIN}, {7, MAX}});
    }

    @Test(order = 2)
    void noFailedSensors() {
        expectCleared(new int[][] {{-6}}, new int[][] {{-6}});
        expectCleared(
                new int[][] {{3, -8, 15, 1, -2, 9}, {7, 4, -11, 6, 20, -5}},
                new int[][] {{3, -8, 15, 1, -2, 9}, {7, 4, -11, 6, 20, -5}});
        expectCleared(new int[][] {{5}, {-3}, {12}, {8}, {-1}, {2}}, new int[][] {{5}, {-3}, {12}, {8}, {-1}, {2}});
    }

    @Test(order = 3)
    void singleCellsRowsAndColumns() {
        expectCleared(new int[][] {{0}}, new int[][] {{0}});
        expectCleared(new int[][] {{0, 0, 0, 0, 0, 0}}, new int[][] {{6, 0, -2, 9, 0, 4}});
        expectCleared(new int[][] {{0}, {0}, {0}, {0}, {0}}, new int[][] {{5}, {8}, {0}, {1}, {-4}});
        int[][] row = new int[1][1000];
        int[][] column = new int[1000][1];
        for (int i = 0; i < 1000; i++) {
            row[0][i] = i == 999 ? 0 : i + 1; // the only 0 is the last reading
            column[i][0] = i == 0 ? 0 : -i; // the only 0 is the first reading
        }
        expectCleared(new int[1][1000], row);
        expectCleared(new int[1000][1], column);
    }

    @Test(order = 4)
    void aZeroInTheFirstRowOnly() {
        expectCleared(
                new int[][] {{0, 0, 0, 0, 0}, {6, 9, 14, 0, -7}, {-4, 11, 1, 0, 5}, {10, -2, 13, 0, 15}},
                new int[][] {{8, 3, -5, 0, 12}, {6, 9, 14, 2, -7}, {-4, 11, 1, 16, 5}, {10, -2, 13, 7, 15}});
    }

    @Test(order = 5)
    void aZeroInTheFirstColumnOnly() {
        expectCleared(
                new int[][] {{0, -6, 17, 4}, {0, 19, -8, 25}, {0, 7, 30, 1}, {0, 0, 0, 0}, {0, -15, 6, 18}},
                new int[][] {{21, -6, 17, 4}, {3, 19, -8, 25}, {-12, 7, 30, 1}, {0, 14, -3, 22}, {9, -15, 6, 18}});
    }

    @Test(order = 6)
    void zerosInTheOtherCorners() {
        expectCleared(
                new int[][] {{5, -9, 4, 13, 8, 0}, {0, 0, 0, 0, 0, 0}},
                new int[][] {{5, -9, 4, 13, 8, 2}, {-1, 6, 11, 3, -4, 0}});
        expectCleared(
                new int[][] {{0, 0, 0, 0, 0, 0}, {0, 14, -20, 9, 31, 0}, {0, -7, 23, 4, -15, 0}, {0, 0, 0, 0, 0, 0}},
                new int[][] {{17, -3, 8, 26, -11, 0}, {5, 14, -20, 9, 31, -6}, {12, -7, 23, 4, -15, 19}, {0, 28, -9, 16, 2, -24}});
    }

    @Test(order = 7)
    void clearedReadingsDoNotSpread() {
        expectCleared(
                new int[][] {{4, 9, -2, 0, 13, 6}, {11, -5, 8, 0, 1, 10}, {0, 0, 0, 0, 0, 0}, {12, 5, -3, 0, 8, 4}, {1, 16, 9, 0, 2, 7}},
                new int[][] {{4, 9, -2, 7, 13, 6}, {11, -5, 8, 3, 1, 10}, {-7, 2, 15, 0, 6, -9}, {12, 5, -3, 14, 8, 4}, {1, 16, 9, -6, 2, 7}});
    }

    @Test(order = 8)
    void anyValueCanBeAReading() {
        // -1 and the smallest and largest int survive here, so none of them can stand for "clear me later"
        expectCleared(
                new int[][] {{MIN, -1, 0, -1, MIN}, {0, 0, 0, 0, 0}, {-1, MAX, 0, MAX, -1}, {MIN, -1, 0, MIN, MAX}},
                new int[][] {{MIN, -1, MAX, -1, MIN}, {MAX, MIN, 0, -1, MAX}, {-1, MAX, MIN, MAX, -1}, {MIN, -1, -1, MIN, MAX}});
    }

    @Test(order = 9)
    void everyRowFails() {
        expectCleared(
                new int[4][5],
                new int[][] {{5, 8, 0, 3, 6}, {2, 7, 4, 9, 0}, {0, 1, 6, 5, 3}, {4, 0, 2, 8, 7}});
        expectCleared(new int[2][3], new int[2][3]);
    }

    @Test(order = 10)
    void randomGrids() {
        Random random = new Random(1381);
        int[] odd = {-1, MIN, MAX, 1};
        for (int round = 0; round < 300; round++) {
            int m = 1 + random.nextInt(12);
            int n = 1 + random.nextInt(12);
            int[][] grid = new int[m][n];
            for (int[] row : grid) {
                for (int c = 0; c < n; c++) {
                    int roll = random.nextInt(20);
                    // about one reading in ten fails; a few are values a marker might borrow
                    row[c] = roll < 2 ? 0 : roll < 4 ? odd[random.nextInt(odd.length)] : 100 + random.nextInt(900);
                }
            }
            expectCleared(cleared(grid), grid);
        }
    }

    @Test(order = 11, timeoutMs = 500)
    void largestGridsWithMostSensorsFailed() {
        // A: every sensor fails except row 500 and column 700, so only the reading where they cross survives
        int[][] a = new int[1000][1000];
        for (int r = 0; r < 1000; r++) {
            for (int c = 0; c < 1000; c++) {
                a[r][c] = r == 500 || c == 700 ? -(1000 * r + c + 1) : 0;
            }
        }
        int[][] wantA = new int[1000][1000];
        wantA[500][700] = a[500][700];
        expectCleared(wantA, a);
        // B: every sensor fails except in rows 0 to 9 and columns 990 to 999, whose 10 x 10 corner survives
        int[][] b = new int[1000][1000];
        int[][] wantB = new int[1000][1000];
        for (int r = 0; r < 1000; r++) {
            for (int c = 0; c < 1000; c++) {
                b[r][c] = r >= 10 && c < 990 ? 0 : MIN + 1000 * r + c;
                if (r < 10 && c >= 990) {
                    wantB[r][c] = b[r][c];
                }
            }
        }
        expectCleared(wantB, b);
    }
}
