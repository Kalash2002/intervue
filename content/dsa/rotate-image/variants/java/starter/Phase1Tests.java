import static kit.Check.assertEquals;
import static kit.Check.assertTrue;

import java.util.Arrays;
import java.util.Random;
import kit.Test;

public class Phase1Tests {

    /** Turns grid with the method under test and checks that grid itself now holds want. */
    private static void expectTurned(int[][] want, int[][] grid) {
        int n = grid.length;
        String input = n <= 6 ? "grid = " + Arrays.deepToString(grid) : n + " x " + n + " grid";
        Board.rotate(grid); // grid still names the caller's array, so only an in-place turn shows here
        assertEquals(n, grid.length, "the number of rows must not change for " + input);
        for (int r = 0; r < n; r++) {
            assertTrue(grid[r] != null && grid[r].length == n, "row " + r + " must still hold " + n + " tiles for " + input);
            for (int c = 0; c < n; c++) {
                if (grid[r][c] != want[r][c]) { // the message is built only for a wrong tile
                    assertEquals(want[r][c], grid[r][c], "the array passed in, row " + r + ", column " + c + ", for " + input);
                }
            }
        }
    }

    /** The turned board, built in a separate array from the rule: row r becomes column n - 1 - r. */
    private static int[][] turned(int[][] grid) {
        int n = grid.length;
        int[][] out = new int[n][n];
        for (int r = 0; r < n; r++) {
            for (int c = 0; c < n; c++) {
                out[c][n - 1 - r] = grid[r][c];
            }
        }
        return out;
    }

    private static int[][] copy(int[][] grid) {
        int[][] out = new int[grid.length][];
        for (int r = 0; r < grid.length; r++) {
            out[r] = grid[r].clone();
        }
        return out;
    }

    @Test(order = 1)
    void sample() {
        expectTurned(
                new int[][] {{33, 19, 5, 11, 8}, {21, 7, 13, 40, 3}, {10, 28, 0, 2, 15}, {16, 4, 31, 9, 22}, {1, 12, 24, 17, 6}},
                new int[][] {{8, 3, 15, 22, 6}, {11, 40, 2, 9, 17}, {5, 13, 0, 31, 24}, {19, 7, 28, 4, 12}, {33, 21, 10, 16, 1}});
        expectTurned(new int[][] {{4, 6}, {9, -1}}, new int[][] {{6, -1}, {4, 9}});
        expectTurned(new int[][] {{-7}}, new int[][] {{-7}});
    }

    @Test(order = 2)
    void anEvenBoard() {
        expectTurned(
                new int[][] {
                    {-24, -44, -83, 91, -16, -26},
                    {46, -6, 37, 71, 31, -7},
                    {30, -8, -46, 79, 52, -40},
                    {-38, -60, -42, 38, 66, -29},
                    {-33, -68, 39, -90, -30, -41},
                    {95, 51, 9, -19, -53, -61}
                },
                new int[][] {
                    {-26, -7, -40, -29, -41, -61},
                    {-16, 31, 52, 66, -30, -53},
                    {91, 71, 79, 38, -90, -19},
                    {-83, 37, -46, -42, 39, 9},
                    {-44, -6, -8, -60, -68, 51},
                    {-24, 46, 30, -38, -33, 95}
                });
    }

    @Test(order = 3)
    void twoTurnsAreHalfATurn() {
        int[][] grid = {
            {-26, -7, -40, -29, -41, -61},
            {-16, 31, 52, 66, -30, -53},
            {91, 71, 79, 38, -90, -19},
            {-83, 37, -46, -42, 39, 9},
            {-44, -6, -8, -60, -68, 51},
            {-24, 46, 30, -38, -33, 95}
        };
        Board.rotate(grid);
        expectTurned(
                new int[][] {
                    {95, -33, -38, 30, 46, -24},
                    {51, -68, -60, -8, -6, -44},
                    {9, 39, -42, -46, 37, -83},
                    {-19, -90, 38, 79, 71, 91},
                    {-53, -30, 66, 52, 31, -16},
                    {-61, -41, -29, -40, -7, -26}
                },
                grid);
    }

    @Test(order = 4)
    void fourTurnsGiveTheBoardBack() {
        int[][] grid = new int[7][7];
        for (int r = 0; r < 7; r++) {
            for (int c = 0; c < 7; c++) {
                grid[r][c] = (7 * r + c) * 17 % 101 - 50; // all different on a 7 x 7 board
            }
        }
        int[][] original = copy(grid);
        Board.rotate(grid);
        Board.rotate(grid);
        Board.rotate(grid);
        expectTurned(original, grid);
    }

    @Test(order = 5)
    void anOddBoardKeepsItsCentre() {
        int[][] grid = new int[7][7];
        for (int r = 0; r < 7; r++) {
            for (int c = 0; c < 7; c++) {
                grid[r][c] = 500 - 13 * (7 * r + c);
            }
        }
        expectTurned(turned(grid), grid);
    }

    @Test(order = 6)
    void repeatedValues() {
        expectTurned(new int[][] {{1000, -1000}, {-1000, 1000}}, new int[][] {{-1000, 1000}, {1000, -1000}});
        int[][] same = new int[5][5];
        for (int[] row : same) {
            Arrays.fill(row, 42);
        }
        expectTurned(copy(same), same);
    }

    @Test(order = 7)
    void theLargestBoard() {
        int[][] grid = new int[20][20];
        for (int r = 0; r < 20; r++) {
            for (int c = 0; c < 20; c++) {
                grid[r][c] = 20 * c - r - 400; // all different, from -419 to -20
            }
        }
        expectTurned(turned(grid), grid);
    }

    @Test(order = 8)
    void randomBoards() {
        Random random = new Random(1591);
        for (int round = 0; round < 200; round++) {
            int n = 1 + random.nextInt(20);
            int[][] grid = new int[n][n];
            for (int[] row : grid) {
                for (int c = 0; c < n; c++) {
                    row[c] = random.nextInt(2001) - 1000;
                }
            }
            expectTurned(turned(grid), grid);
        }
    }
}
