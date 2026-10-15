import static kit.Check.assertEquals;

import kit.Test;

public class Phase1Tests {

    @Test(order = 1)
    void sample() {
        assertEquals(7, Basin.trapped(new int[] {3, 0, 2, 0, 4}));
    }

    @Test(order = 2)
    void tooSmallToHoldWater() {
        assertEquals(0, Basin.trapped(new int[] {}));
        assertEquals(0, Basin.trapped(new int[] {5}));
        assertEquals(0, Basin.trapped(new int[] {2, 7}));
    }

    @Test(order = 3)
    void onlyRisingOrFalling() {
        assertEquals(0, Basin.trapped(new int[] {1, 2, 3}));
        assertEquals(0, Basin.trapped(new int[] {3, 2, 1}));
    }

    @Test(order = 4)
    void flatRow() {
        assertEquals(0, Basin.trapped(new int[] {5, 5, 5}));
    }

    @Test(order = 5)
    void simpleBasins() {
        assertEquals(6, Basin.trapped(new int[] {4, 1, 1, 4}));
        assertEquals(2, Basin.trapped(new int[] {2, 0, 2}));
    }

    @Test(order = 6)
    void twoBasinsOfDifferentDepth() {
        assertEquals(11, Basin.trapped(new int[] {5, 1, 3, 1, 4, 2, 0, 3}));
    }

    @Test(order = 7)
    void plateausAndDips() {
        assertEquals(11, Basin.trapped(new int[] {0, 3, 0, 0, 3, 3, 0, 1, 0, 2}));
    }

    @Test(order = 8)
    void tallWallInTheMiddleWithLowerSides() {
        assertEquals(7, Basin.trapped(new int[] {1, 0, 2, 5, 0, 0, 3, 1}));
    }

    @Test(order = 9, timeoutMs = 1000)
    void largeDeepBasin() {
        int[] heights = new int[100_000];
        heights[0] = 10_000;
        heights[heights.length - 1] = 10_000;
        assertEquals(999_980_000, Basin.trapped(heights));
    }

    @Test(order = 10, timeoutMs = 1000)
    void largeAlternatingWalls() {
        int[] heights = new int[100_000];
        for (int i = 0; i < heights.length; i += 2) {
            heights[i] = 10_000; // walls at even positions, gaps at odd ones; the last gap has no wall after it
        }
        assertEquals(499_990_000, Basin.trapped(heights));
    }
}
