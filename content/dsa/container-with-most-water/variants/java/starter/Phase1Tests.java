import static kit.Check.assertEquals;

import java.util.Random;
import kit.Test;

public class Phase1Tests {

    @Test(order = 1)
    void sample() {
        assertEquals(28, Containers.maxVolume(new int[] {3, 9, 2, 8, 4, 7, 1}));
    }

    @Test(order = 2)
    void twoWalls() {
        assertEquals(5, Containers.maxVolume(new int[] {5, 6}));
    }

    @Test(order = 3)
    void emptySpaceBetweenTheWalls() {
        assertEquals(12, Containers.maxVolume(new int[] {4, 0, 0, 4}));
    }

    @Test(order = 4)
    void allWallsEqual() {
        assertEquals(24, Containers.maxVolume(new int[] {6, 6, 6, 6, 6}));
    }

    @Test(order = 5)
    void ascendingHeights() {
        assertEquals(9, Containers.maxVolume(new int[] {1, 2, 3, 4, 5, 6}));
    }

    @Test(order = 6)
    void descendingHeights() {
        assertEquals(20, Containers.maxVolume(new int[] {9, 8, 7, 6, 5, 4, 3, 2, 1}));
    }

    @Test(order = 7)
    void wallsOfHeightZero() {
        assertEquals(0, Containers.maxVolume(new int[] {0, 0}));
        assertEquals(0, Containers.maxVolume(new int[] {0, 5}));
    }

    @Test(order = 8)
    void tallMiddleWallsLoseToAWiderPair() {
        assertEquals(16, Containers.maxVolume(new int[] {2, 1, 1, 1, 1, 9, 9, 1, 2}));
    }

    @Test(order = 9, timeoutMs = 1000)
    void largeOuterPairWins() {
        Random random = new Random(13);
        int[] heights = new int[200_000];
        for (int i = 0; i < heights.length; i++) {
            heights[i] = random.nextInt(10_000); // below 10,000, so no inner wall can match the outer ones
        }
        heights[0] = 10_000;
        heights[heights.length - 1] = 10_000;
        assertEquals(1_999_990_000, Containers.maxVolume(heights));
    }

    @Test(order = 10, timeoutMs = 1000)
    void largeTwoTallWallsAmongZeros() {
        int[] heights = new int[200_000];
        heights[70_000] = 10_000;
        heights[150_000] = 10_000;
        heights[100_000] = 9_999;
        assertEquals(800_000_000, Containers.maxVolume(heights));
    }
}
