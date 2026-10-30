import static kit.Check.assertEquals;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Random;
import kit.Test;

public class Phase1Tests {

    private static void expect(int want, int[][] heights) {
        String shown = heights.length * heights[0].length <= 30
                ? Arrays.deepToString(heights)
                : heights.length + " by " + heights[0].length + " grid";
        int[][] copy = new int[heights.length][];
        for (int r = 0; r < heights.length; r++) {
            copy[r] = heights[r].clone();
        }
        assertEquals(want, Hike.longestClimb(copy), "heights = " + shown);
    }

    @Test(order = 1)
    void sample() {
        expect(9, new int[][] {{5, 6, 7, 2}, {4, 9, 8, 1}, {3, 2, 0, 1}});
        expect(6, new int[][] {{8, 1, 2, 3, 4}, {0, 9, 9, 9, 5}});
        expect(1, new int[][] {{4, 4}, {4, 4}, {4, 4}});
    }

    @Test(order = 2)
    void takingTheLowestOrTheHighestNextStepFallsShort() {
        // 1, 2, 5, 6 along the top, then down to the 7 and right to the 8; stepping to the lowest
        // higher neighbour finds at most 1, 2, 4, 8, and to the highest at most 2, 5, 6, 8, four
        // cells each, and the 0 starts a climb of only five
        expect(6, new int[][] {{1, 2, 5, 6, 8}, {8, 4, 0, 7, 8}});
    }

    @Test(order = 3)
    void diagonalStepsDoNotCount() {
        // 1, 2, 3 and 4 rise along the diagonal, but every side neighbour is a 9
        expect(2, new int[][] {{1, 9, 9, 9}, {9, 2, 9, 9}, {9, 9, 3, 9}, {9, 9, 9, 4}});
    }

    @Test(order = 4)
    void equalHeightsAreNotAClimb() {
        expect(2, new int[][] {{1, 2, 2, 3}});
        // stepping onto equal heights as well would walk through all six cells
        expect(3, new int[][] {{5, 5, 6}, {5, 5, 7}});
    }

    @Test(order = 5)
    void aSingleRowColumnOrCell() {
        expect(3, new int[][] {{2, 7, 4, 6, 8, 3}});
        expect(4, new int[][] {{6}, {2}, {5}, {7}, {8}});
        expect(1, new int[][] {{2_147_483_647}});
    }

    @Test(order = 6)
    void heightsAtTheEndsOfTheRange() {
        // 0, 1, 7, then 2147483645 and 2147483646; the 2147483647 ends a climb of four
        expect(5, new int[][] {{0, 1, 2_147_483_646}, {2_147_483_647, 7, 2_147_483_645}});
    }

    @Test(order = 7)
    void randomSmallGridsAgainstEveryClimb() {
        Random random = new Random(1217);
        for (int round = 0; round < 300; round++) {
            int rows = 1 + random.nextInt(5);
            int cols = 1 + random.nextInt(5);
            if (rows == 3 && cols == 3) {
                cols++; // no 3 by 3 grids
            }
            int[][] heights = new int[rows][cols];
            for (int[] row : heights) {
                for (int c = 0; c < cols; c++) {
                    row[c] = random.nextInt(7); // few heights: many equal neighbours
                }
            }
            expect(everyClimb(heights), heights);
        }
    }

    /** Follows every rising walk from every cell, one stack entry per walk, and keeps the longest. */
    private static int everyClimb(int[][] heights) {
        int best = 0;
        ArrayDeque<int[]> walks = new ArrayDeque<>(); // {row, column, cells so far}
        for (int r = 0; r < heights.length; r++) {
            for (int c = 0; c < heights[0].length; c++) {
                walks.push(new int[] {r, c, 1});
            }
        }
        int[][] steps = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        while (!walks.isEmpty()) {
            int[] walk = walks.pop();
            best = Math.max(best, walk[2]);
            for (int[] step : steps) {
                int r = walk[0] + step[0];
                int c = walk[1] + step[1];
                if (r >= 0 && c >= 0 && r < heights.length && c < heights[0].length
                        && heights[r][c] > heights[walk[0]][walk[1]]) {
                    walks.push(new int[] {r, c, walk[2] + 1});
                }
            }
        }
        return best;
    }

    /**
     * A 50 by 50 grid of 2147483647 with a corridor of rising heights winding through it: along rows
     * 0, 2, ..., 48, turning down through one cell at alternate ends. The corridor cells are 1 to 1274
     * in order along it, or in the opposite order.
     */
    private static int[][] corridor(boolean risingFromTheTopLeft) {
        int n = 50;
        int[][] heights = new int[n][n];
        for (int[] row : heights) {
            Arrays.fill(row, Integer.MAX_VALUE);
        }
        int along = 0;
        for (int r = 0; r < n - 1; r += 2) {
            boolean rightwards = r % 4 == 0;
            for (int i = 0; i < n; i++) {
                along++;
                heights[r][rightwards ? i : n - 1 - i] = risingFromTheTopLeft ? along : 1275 - along;
            }
            if (r + 2 < n - 1) {
                along++;
                heights[r + 1][rightwards ? n - 1 : 0] = risingFromTheTopLeft ? along : 1275 - along;
            }
        }
        return heights;
    }

    @Test(order = 8)
    void aWindingCorridorNumberedBothWays() {
        // the 1274 corridor cells in order, then a step up into the wall beside the last of them
        expect(1275, corridor(true));
        expect(1275, corridor(false));
    }

    /** 50 by 50, rising by 1 along each row and down to the next row's end: 0 to 2499 in a snake. */
    private static int[][] snake(boolean risingFromTheTopLeft) {
        int n = 50;
        int[][] heights = new int[n][n];
        for (int r = 0; r < n; r++) {
            for (int c = 0; c < n; c++) {
                int along = r * n + (r % 2 == 0 ? c : n - 1 - c);
                heights[r][c] = risingFromTheTopLeft ? along : n * n - 1 - along;
            }
        }
        return heights;
    }

    @Test(order = 9, timeoutMs = 200)
    void aSnakeThroughEveryCell() {
        // every step down a column rises too, so rising walks branch at every cell
        expect(2500, snake(true));
    }

    @Test(order = 10, timeoutMs = 200)
    void theSameSnakeFallingFromTheTopLeft() {
        expect(2500, snake(false));
    }

    @Test(order = 11, timeoutMs = 200)
    void aPyramid() {
        // the peak is 100 at row 25, column 24, and each step away from it is 1 lower; the corner
        // at row 0, column 49 is 50 steps away, and every climb rises by exactly 1 a step
        int n = 50;
        int[][] heights = new int[n][n];
        for (int r = 0; r < n; r++) {
            for (int c = 0; c < n; c++) {
                heights[r][c] = 100 - Math.abs(r - 25) - Math.abs(c - 24);
            }
        }
        expect(51, heights);
    }
}
