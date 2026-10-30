import static kit.Check.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import kit.Test;

public class Phase1Tests {

    @Test(order = 1)
    void sample() {
        assertEquals(14L, Field.minCable(new int[][] {{0, 0}, {3, 1}, {1, 4}, {5, 5}}));
        assertEquals(15L, Field.minCable(new int[][] {{2, -3}, {-1, 2}, {4, 4}}));
    }

    @Test(order = 2)
    void oneAndTwoSensors() {
        assertEquals(0L, Field.minCable(new int[][] {{7, 7}}));
        assertEquals(14L, Field.minCable(new int[][] {{-5, 2}, {3, -4}}));
    }

    @Test(order = 3)
    void sensorsInALineOutOfOrder() {
        // joining each sensor to the first, or to the nearest one listed before it, costs more than 12
        assertEquals(12L, Field.minCable(new int[][] {{0, 0}, {0, 7}, {0, 3}, {0, 12}}));
    }

    @Test(order = 4)
    void cableRunsAlongTheGridNotStraight() {
        // straight-line lengths would give 5 + 5 = 10; along the grid it is 6 + 7
        assertEquals(13L, Field.minCable(new int[][] {{0, 0}, {3, 4}, {6, 0}}));
    }

    @Test(order = 5)
    void farCorners() {
        assertEquals(4_000_000L, Field.minCable(new int[][] {{-1_000_000, -1_000_000}, {1_000_000, 1_000_000}, {1_000_000, -1_000_000}}));
    }

    /** A rows x cols grid, 3 apart across and 5 apart down, shuffled; the best plan costs rows*(cols-1)*3 + (rows-1)*5. */
    private static int[][] grid(int rows, int cols, int seed) {
        List<int[]> points = new ArrayList<>();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                points.add(new int[] {-70 + 3 * c, 40 - 5 * r});
            }
        }
        Collections.shuffle(points, new Random(seed));
        return points.toArray(new int[0][]);
    }

    @Test(order = 6)
    void aSmallGridOfSensors() {
        assertEquals(115L, Field.minCable(grid(6, 6, 3))); // 6*5*3 + 5*5
    }

    @Test(order = 7)
    void aDiagonalStaircase() {
        // each step is 2 across and 1 up, so neighbours are 3 apart and the best plan follows the stairs
        int[][] points = new int[30][];
        for (int i = 0; i < 30; i++) {
            points[(i * 7) % 30] = new int[] {2 * i, i};
        }
        assertEquals(87L, Field.minCable(points)); // 29 steps of 3
    }

    @Test(order = 8, timeoutMs = 600)
    void twoThousandSensors() {
        assertEquals(6075L, Field.minCable(grid(40, 50, 11))); // 40*49*3 + 39*5
    }
}
