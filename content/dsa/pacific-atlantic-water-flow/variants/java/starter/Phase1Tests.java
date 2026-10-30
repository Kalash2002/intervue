import static kit.Check.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import kit.Test;

public class Phase1Tests {

    /** Sorts the cells returned by row, then column, and writes them as "[r,c] [r,c] ...". */
    private static String normalised(List<int[]> cells) {
        List<int[]> sorted = new ArrayList<>(cells);
        sorted.sort((a, b) -> a[0] != b[0] ? Integer.compare(a[0], b[0]) : Integer.compare(a[1], b[1]));
        StringBuilder out = new StringBuilder();
        for (int[] cell : sorted) {
            out.append('[').append(cell[0]).append(',').append(cell[1]).append("] ");
        }
        return out.toString().trim();
    }

    private static void expect(int[][] heights, int[][] want) {
        List<int[]> wanted = new ArrayList<>(List.of(want));
        assertEquals(normalised(wanted), normalised(WaterFlow.bothOceans(heights)), "the cells, sorted by row then column");
    }

    @Test(order = 1)
    void sample() {
        expect(new int[][] {{1, 3, 4}, {2, 5, 3}, {6, 2, 1}}, new int[][] {{0, 2}, {1, 1}, {2, 0}});
    }

    @Test(order = 2)
    void waterFlowsBetweenEqualHeights() {
        expect(new int[][] {{2, 2}, {2, 2}}, new int[][] {{0, 0}, {0, 1}, {1, 0}, {1, 1}});
        // the ring of 3s is one level: a version that needs a strictly lower step strands most of it
        expect(new int[][] {{3, 3, 3}, {3, 1, 3}, {3, 3, 3}},
                new int[][] {{0, 0}, {0, 1}, {0, 2}, {1, 0}, {1, 2}, {2, 0}, {2, 1}, {2, 2}});
    }

    @Test(order = 3)
    void singleRowColumnAndCell() {
        expect(new int[][] {{4, 8, 1}}, new int[][] {{0, 0}, {0, 1}, {0, 2}});
        expect(new int[][] {{7}, {0}, {7}}, new int[][] {{0, 0}, {1, 0}, {2, 0}});
        expect(new int[][] {{0}}, new int[][] {{0, 0}});
    }

    @Test(order = 4)
    void theTwoCornersThatTouchBothOceans() {
        expect(new int[][] {{1, 9}, {9, 1}}, new int[][] {{0, 1}, {1, 0}});
    }

    @Test(order = 5)
    void waterRunsDownhillNotUp() {
        // the 0 is a pit and the 2 can only drain into it or the Atlantic; a version that floods
        // from the oceans downhill instead of uphill gets both wrong
        expect(new int[][] {{5, 6, 7}, {4, 0, 8}, {3, 2, 9}},
                new int[][] {{0, 0}, {0, 1}, {0, 2}, {1, 0}, {1, 2}, {2, 0}, {2, 2}});
    }

    @Test(order = 6)
    void randomSmallMapsAgainstOneSearchPerCell() {
        Random random = new Random(83);
        for (int round = 0; round < 20; round++) {
            int rows = 1 + random.nextInt(10);
            int cols = 1 + random.nextInt(10);
            int[][] heights = new int[rows][cols];
            for (int[] row : heights) {
                for (int c = 0; c < cols; c++) {
                    row[c] = random.nextInt(5); // few heights: many equal neighbours
                }
            }
            List<int[]> want = new ArrayList<>();
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    if (reaches(heights, r, c, true) && reaches(heights, r, c, false)) {
                        want.add(new int[] {r, c});
                    }
                }
            }
            expect(heights, want.toArray(new int[0][]));
        }
    }

    @Test(order = 7, timeoutMs = 1000)
    void walledBasin() {
        int n = 200;
        int[][] heights = new int[n][n]; // all 0 to start
        for (int i = 1; i < n - 1; i++) { // a wall of 100,000 one cell in from the border
            heights[1][i] = 100_000;
            heights[n - 2][i] = 100_000;
            heights[i][1] = 100_000;
            heights[i][n - 2] = 100_000;
        }
        List<int[]> want = new ArrayList<>();
        for (int r = 0; r < n; r++) {
            for (int c = 0; c < n; c++) {
                if (r < 2 || c < 2 || r >= n - 2 || c >= n - 2) { // the basin inside the wall drains nowhere
                    want.add(new int[] {r, c});
                }
            }
        }
        expect(heights, want.toArray(new int[0][]));
    }

    @Test(order = 8, timeoutMs = 1000)
    void oneLongWindingSlope() {
        int n = 200;
        int[][] heights = new int[n][n];
        for (int r = 0; r < n; r++) {
            for (int c = 0; c < n; c++) {
                heights[r][c] = r * n + (r % 2 == 0 ? c : n - 1 - c); // rises along a path that snakes down the rows
            }
        }
        List<int[]> want = new ArrayList<>();
        want.add(new int[] {0, n - 1}); // the rest of the top row only drains left, to the Pacific
        for (int r = 1; r < n; r++) {
            for (int c = 0; c < n; c++) {
                want.add(new int[] {r, c});
            }
        }
        expect(heights, want.toArray(new int[0][]));
    }

    @Test(order = 9, timeoutMs = 1000)
    void oneLargeLevelPlain() {
        int n = 200;
        int[][] heights = new int[n][n];
        List<int[]> want = new ArrayList<>();
        for (int r = 0; r < n; r++) {
            for (int c = 0; c < n; c++) {
                heights[r][c] = 7; // all level: water crosses the whole map, and a search can wind 40,000 cells deep
                want.add(new int[] {r, c});
            }
        }
        expect(heights, want.toArray(new int[0][]));
    }

    // Whether water from (r, c) can reach the Pacific (top or left edge) or the Atlantic (bottom or right edge).
    private static boolean reaches(int[][] heights, int r, int c, boolean pacific) {
        int rows = heights.length;
        int cols = heights[0].length;
        boolean[][] seen = new boolean[rows][cols];
        List<int[]> todo = new ArrayList<>();
        todo.add(new int[] {r, c});
        seen[r][c] = true;
        for (int i = 0; i < todo.size(); i++) {
            int[] cell = todo.get(i);
            if (pacific ? cell[0] == 0 || cell[1] == 0 : cell[0] == rows - 1 || cell[1] == cols - 1) {
                return true;
            }
            int[][] next = {{cell[0] + 1, cell[1]}, {cell[0] - 1, cell[1]}, {cell[0], cell[1] + 1}, {cell[0], cell[1] - 1}};
            for (int[] nb : next) {
                if (nb[0] >= 0 && nb[1] >= 0 && nb[0] < rows && nb[1] < cols && !seen[nb[0]][nb[1]]
                        && heights[nb[0]][nb[1]] <= heights[cell[0]][cell[1]]) {
                    seen[nb[0]][nb[1]] = true;
                    todo.add(nb);
                }
            }
        }
        return false;
    }
}
