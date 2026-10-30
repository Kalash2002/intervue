import static kit.Check.assertEquals;
import static kit.Check.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import kit.Test;

public class Phase1Tests {

    /** Compares reading by reading, then the number of readings, and checks that grid is unchanged. */
    private static void expect(int[][] grid, int... want) {
        int[][] before = copy(grid);
        int m = grid.length;
        int n = grid[0].length;
        String input = m * n <= 40 ? "grid = " + Arrays.deepToString(grid) : m + " x " + n + " grid";
        List<Integer> got = Sweep.spiralOrder(grid);
        assertTrue(got != null, "return a list of readings, not null, for " + input);
        for (int i = 0; i < Math.min(want.length, got.size()); i++) {
            Integer reading = got.get(i);
            if (reading == null || reading != want[i]) { // the message is built only for a wrong reading
                assertEquals((Object) want[i], reading, "reading " + i + " for " + input);
            }
        }
        assertEquals(want.length, got.size(), "number of readings for " + input);
        assertEquals(Arrays.deepToString(before), Arrays.deepToString(grid), "grid must not change for " + input);
    }

    private static int[][] copy(int[][] grid) {
        int[][] out = new int[grid.length][];
        for (int r = 0; r < grid.length; r++) {
            out[r] = grid[r].clone();
        }
        return out;
    }

    /** Walks the floor tile by tile, turning clockwise at the edge or at a cleaned tile. */
    private static int[] walk(int[][] grid) {
        int m = grid.length;
        int n = grid[0].length;
        boolean[][] cleaned = new boolean[m][n];
        int[] dr = {0, 1, 0, -1};
        int[] dc = {1, 0, -1, 0};
        int[] out = new int[m * n];
        int r = 0;
        int c = 0;
        int d = 0;
        for (int i = 0; i < m * n; i++) {
            out[i] = grid[r][c];
            cleaned[r][c] = true;
            int nr = r + dr[d];
            int nc = c + dc[d];
            if (nr < 0 || nr >= m || nc < 0 || nc >= n || cleaned[nr][nc]) {
                d = (d + 1) % 4;
                nr = r + dr[d];
                nc = c + dc[d];
            }
            r = nr;
            c = nc;
        }
        return out;
    }

    @Test(order = 1)
    void sample() {
        expect(
                new int[][] {{12, 7, 30, 4, 18}, {25, 9, 41, 16, 2}, {6, 33, 14, 27, 11}, {19, 1, 22, 8, 35}},
                12, 7, 30, 4, 18, 2, 11, 35, 8, 22, 1, 19, 6, 25, 9, 41, 16, 27, 14, 33);
        expect(new int[][] {{3, -4}, {10, 6}, {-8, 15}, {2, 0}, {7, -1}}, 3, -4, 6, 15, 0, -1, 7, 2, -8, 10);
        expect(new int[][] {{4}, {-2}, {9}}, 4, -2, 9);
    }

    @Test(order = 2)
    void oneRowOrOneTile() {
        expect(new int[][] {{5, -3, 8, 0}}, 5, -3, 8, 0);
        expect(new int[][] {{42}}, 42);
    }

    @Test(order = 3)
    void oneColumn() {
        expect(new int[][] {{30}, {-12}, {45}, {0}}, 30, -12, 45, 0);
        expect(new int[][] {{7}, {7}}, 7, 7);
    }

    @Test(order = 4)
    void aMiddleRowIsLeftOver() {
        // after two rings, only the middle row's three inner tiles are left: -38, -99, 58
        expect(
                new int[][] {
                    {-69, -18, 29, 32, 66, -73, -42},
                    {54, 60, 43, 8, 47, 41, 26},
                    {51, 13, -38, -99, 58, -79, -71},
                    {-26, -74, 16, -97, 86, -19, -46},
                    {2, -35, -10, -8, -3, 96, 64}
                },
                -69, -18, 29, 32, 66, -73, -42, 26, -71, -46, 64, 96, -3, -8, -10, -35, 2, -26, 51, 54, 60, 43, 8, 47,
                41, -79, -19, 86, -97, 16, -74, 13, -38, -99, 58);
    }

    @Test(order = 5)
    void aMiddleColumnIsLeftOver() {
        // after two rings, only the middle column's three inner tiles are left: 84, -5, 3
        expect(
                new int[][] {
                    {-80, 86, -12, -77, 43},
                    {38, -25, -26, 18, -63},
                    {66, 81, 84, 48, -20},
                    {-93, 82, -5, -6, 19},
                    {9, -76, 3, 50, 42},
                    {28, -70, 79, 30, 54},
                    {26, 2, 35, -33, 8}
                },
                -80, 86, -12, -77, 43, -63, -20, 19, 42, 54, 8, -33, 35, 2, 26, 28, 9, -93, 66, 38, -25, -26, 18, 48,
                -6, 50, 30, 79, -70, -76, 82, 81, 84, -5, 3);
    }

    @Test(order = 6)
    void anOddSquareEndsInTheCentre() {
        expect(
                new int[][] {
                    {45, 23, 32, 33, -93}, {48, -42, -66, -87, 81}, {34, 72, -73, 84, 60}, {10, 21, -63, 24, -41}, {-68, 61, 17, 73, 83}
                },
                45, 23, 32, 33, -93, 81, 60, -41, 83, 73, 17, 61, -68, 10, 34, 48, -42, -66, -87, 84, 24, -63, 21, 72, -73);
    }

    @Test(order = 7)
    void twoRowsOrTwoColumns() {
        expect(new int[][] {{83, 40, -35, 44, 3, 72}, {-21, 41, 66, 29, 79, 23}}, 83, 40, -35, 44, 3, 72, 23, 79, 29, 66, 41, -21);
        expect(
                new int[][] {{3, 11}, {-48, -24}, {-31, -97}, {-91, -61}, {68, 53}, {18, 34}},
                3, 11, -24, -97, -61, 53, 34, 18, 68, -91, -31, -48);
    }

    @Test(order = 8)
    void equalReadingsAreStillCountedOnce() {
        int[][] grid = new int[4][6];
        for (int[] row : grid) {
            Arrays.fill(row, 9);
        }
        int[] want = new int[24];
        Arrays.fill(want, 9);
        expect(grid, want);
    }

    @Test(order = 9)
    void theLargestFloors() {
        for (int[] size : new int[][] {{100, 100}, {1, 100}, {100, 1}, {100, 99}, {99, 100}}) {
            int[][] grid = new int[size[0]][size[1]];
            for (int r = 0; r < size[0]; r++) {
                for (int c = 0; c < size[1]; c++) {
                    grid[r][c] = (r * 131 + c * 17) % 2001 - 1000;
                }
            }
            expect(grid, walk(grid));
        }
    }

    @Test(order = 10)
    void randomFloors() {
        Random random = new Random(1603);
        for (int round = 0; round < 300; round++) {
            int[][] grid = new int[1 + random.nextInt(12)][1 + random.nextInt(12)];
            for (int[] row : grid) {
                for (int c = 0; c < row.length; c++) {
                    row[c] = random.nextInt(2001) - 1000;
                }
            }
            expect(grid, walk(grid));
        }
    }
}
