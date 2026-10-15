import static kit.Check.assertEquals;

import java.util.Arrays;
import kit.Test;

public class Phase1Tests {

    @Test(order = 1)
    void sample() {
        assertEquals(6, Skyline.biggestBlock(new int[] {3, 1, 3, 2, 2}));
    }

    @Test(order = 2)
    void emptyAndSingleBar() {
        assertEquals(0, Skyline.biggestBlock(new int[] {}));
        assertEquals(7, Skyline.biggestBlock(new int[] {7}));
    }

    @Test(order = 3)
    void allBarsHaveHeightZero() {
        assertEquals(0, Skyline.biggestBlock(new int[] {0, 0, 0}));
    }

    @Test(order = 4)
    void flatSkyline() {
        assertEquals(6, Skyline.biggestBlock(new int[] {2, 2, 2}));
    }

    @Test(order = 5)
    void risingBarsNeedTheFinalFlush() {
        assertEquals(9, Skyline.biggestBlock(new int[] {1, 2, 3, 4, 5}));
    }

    @Test(order = 6)
    void fallingBars() {
        assertEquals(9, Skyline.biggestBlock(new int[] {5, 4, 3, 2, 1}));
    }

    @Test(order = 7)
    void widerBeatsTaller() {
        assertEquals(12, Skyline.biggestBlock(new int[] {6, 2, 5, 4, 5, 1, 6}));
    }

    @Test(order = 8)
    void shortestBarSpansEverything() {
        assertEquals(16, Skyline.biggestBlock(new int[] {4, 9, 5, 6}));
    }

    @Test(order = 9)
    void repeatedHeights() {
        assertEquals(12, Skyline.biggestBlock(new int[] {4, 4, 4, 2, 4, 4}));
        assertEquals(6, Skyline.biggestBlock(new int[] {3, 3, 1, 3, 3}));
    }

    @Test(order = 10, timeoutMs = 400)
    void largeFlatSkyline() {
        int[] heights = new int[100_000];
        Arrays.fill(heights, 10_000);
        assertEquals(1_000_000_000, Skyline.biggestBlock(heights));
    }

    @Test(order = 11, timeoutMs = 400)
    void largeFallingStaircase() {
        int[] heights = new int[100_000];
        for (int i = 0; i < heights.length; i++) {
            heights[i] = 10_000 - i / 10; // falls by one every ten bars; a rectangle ending at bar i starts at 0
        }
        assertEquals(250_050_000, Skyline.biggestBlock(heights));
    }
}
