import static kit.Check.assertEquals;

import java.util.Arrays;
import java.util.Random;
import kit.Test;

public class Phase1Tests {

    @Test(order = 1)
    void sample() {
        assertEquals(5, Spoilage.minutesUntilSpoiled(new int[][] {{1, 1, 0, 2}, {0, 1, 1, 1}, {1, 1, 0, 0}}));
    }

    @Test(order = 2)
    void oneMinuteForOneNeighbour() {
        assertEquals(1, Spoilage.minutesUntilSpoiled(new int[][] {{2, 1}}));
        assertEquals(1, Spoilage.minutesUntilSpoiled(new int[][] {{0, 1, 0}, {1, 2, 1}, {0, 1, 0}}));
    }

    @Test(order = 3)
    void anOrangeTurnedThisMinuteSpreadsOnlyNextMinute() {
        // a scan that updates the crate in place would spoil a whole row in one minute
        assertEquals(3, Spoilage.minutesUntilSpoiled(new int[][] {{2, 1, 1, 1}}));
        assertEquals(3, Spoilage.minutesUntilSpoiled(new int[][] {{1, 1, 1, 2}}));
        assertEquals(3, Spoilage.minutesUntilSpoiled(new int[][] {{2}, {1}, {1}, {1}}));
        assertEquals(3, Spoilage.minutesUntilSpoiled(new int[][] {{1}, {1}, {1}, {2}}));
    }

    @Test(order = 4)
    void mouldSpreadsFromEverySourceAtOnce() {
        assertEquals(2, Spoilage.minutesUntilSpoiled(new int[][] {{2, 1, 1, 1, 1, 2}}));
        assertEquals(2, Spoilage.minutesUntilSpoiled(new int[][] {{2, 1, 1}, {1, 1, 1}, {1, 1, 2}}));
    }

    @Test(order = 5)
    void anOrangeNoMouldCanReach() {
        assertEquals(-1, Spoilage.minutesUntilSpoiled(new int[][] {{1, 0}, {0, 2}}));
        assertEquals(-1, Spoilage.minutesUntilSpoiled(new int[][] {{2, 1, 0, 1}}));
        assertEquals(-1, Spoilage.minutesUntilSpoiled(new int[][] {{1, 1}, {0, 1}}));
    }

    @Test(order = 6)
    void nothingFreshNeedsNoTime() {
        assertEquals(0, Spoilage.minutesUntilSpoiled(new int[][] {{0, 0}, {0, 0}}));
        assertEquals(0, Spoilage.minutesUntilSpoiled(new int[][] {{2, 2, 0}}));
        assertEquals(0, Spoilage.minutesUntilSpoiled(new int[][] {{0}}));
    }

    @Test(order = 7)
    void theGridIsNotChanged() {
        int[][] grid = {{2, 1, 1}, {0, 0, 1}};
        assertEquals(3, Spoilage.minutesUntilSpoiled(grid));
        assertEquals("2,1,1/0,0,1", text(grid), "the grid after the call");
    }

    @Test(order = 8)
    void randomSmallCratesAgainstAMinuteByMinuteSimulation() {
        Random random = new Random(85);
        for (int round = 0; round < 40; round++) {
            int[][] grid = new int[1 + random.nextInt(8)][1 + random.nextInt(8)];
            for (int[] row : grid) {
                for (int c = 0; c < row.length; c++) {
                    int roll = random.nextInt(10);
                    row[c] = roll < 2 ? 0 : roll < 3 ? 2 : 1;
                }
            }
            String before = text(grid);
            assertEquals(simulate(grid), Spoilage.minutesUntilSpoiled(grid), "crate " + before);
        }
    }

    @Test(order = 9, timeoutMs = 2000)
    void mouldWindingThroughAMillionSlots() {
        // even rows are full; odd rows hold one orange that joins them, alternately on the right and the left,
        // so the 500,500 oranges form one chain and the mould at its start needs one minute per link
        int size = 1000;
        int[][] grid = new int[size][size];
        for (int r = 0; r < size; r++) {
            if (r % 2 == 0) {
                Arrays.fill(grid[r], 1);
            } else {
                grid[r][r % 4 == 1 ? size - 1 : 0] = 1;
            }
        }
        grid[0][0] = 2;
        assertEquals(500_499, Spoilage.minutesUntilSpoiled(grid));
    }

    @Test(order = 10, timeoutMs = 2000)
    void aQuarterOfAMillionMouldySources() {
        // every fourth column is mouldy; the last column is three steps from the nearest mouldy one
        int size = 1000;
        int[][] grid = new int[size][size];
        for (int[] row : grid) {
            for (int c = 0; c < size; c++) {
                row[c] = c % 4 == 0 ? 2 : 1;
            }
        }
        assertEquals(3, Spoilage.minutesUntilSpoiled(grid));
    }

    private static String text(int[][] grid) {
        StringBuilder out = new StringBuilder();
        for (int r = 0; r < grid.length; r++) {
            if (r > 0) {
                out.append('/');
            }
            for (int c = 0; c < grid[r].length; c++) {
                out.append(c > 0 ? "," : "").append(grid[r][c]);
            }
        }
        return out.toString();
    }

    // The rule as stated: each minute, every fresh orange next to a mouldy one turns, read from a snapshot.
    private static int simulate(int[][] start) {
        int[][] now = new int[start.length][];
        for (int r = 0; r < start.length; r++) {
            now[r] = start[r].clone();
        }
        for (int minutes = 0; ; minutes++) {
            int[][] next = new int[now.length][];
            boolean changed = false;
            boolean fresh = false;
            for (int r = 0; r < now.length; r++) {
                next[r] = now[r].clone();
                for (int c = 0; c < now[r].length; c++) {
                    if (now[r][c] != 1) {
                        continue;
                    }
                    boolean touched = (r > 0 && now[r - 1][c] == 2) || (r + 1 < now.length && now[r + 1][c] == 2)
                            || (c > 0 && now[r][c - 1] == 2) || (c + 1 < now[r].length && now[r][c + 1] == 2);
                    next[r][c] = touched ? 2 : 1;
                    changed |= touched;
                    fresh |= !touched;
                }
            }
            if (!changed) {
                return fresh ? -1 : minutes;
            }
            now = next;
        }
    }
}
