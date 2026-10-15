import static kit.Check.assertEquals;

import java.util.Random;
import kit.Test;

public class Phase1Tests {

    @Test(order = 1)
    void sample() {
        assertEquals(4, Streaks.longest(new int[] {41, 7, 40, 39, 8, 38, 9, 500}));
    }

    @Test(order = 2)
    void emptyArray() {
        assertEquals(0, Streaks.longest(new int[] {}));
    }

    @Test(order = 3)
    void singleValue() {
        assertEquals(1, Streaks.longest(new int[] {42}));
    }

    @Test(order = 4)
    void repeatedValuesCountOnce() {
        assertEquals(1, Streaks.longest(new int[] {5, 5, 5}));
        assertEquals(4, Streaks.longest(new int[] {3, 4, 4, 5, 5, 5, 6}));
    }

    @Test(order = 5)
    void negativeValuesAcrossZero() {
        assertEquals(5, Streaks.longest(new int[] {-2, -1, 0, 1, 2, 9}));
        assertEquals(1, Streaks.longest(new int[] {-3, 0}));
    }

    @Test(order = 6)
    void noNeighbours() {
        assertEquals(1, Streaks.longest(new int[] {10, 20, 30}));
    }

    @Test(order = 7)
    void gapsNextToARun() {
        assertEquals(3, Streaks.longest(new int[] {1, 2, 3, 5, 6}));
        assertEquals(3, Streaks.longest(new int[] {1, 2, 4, 5, 6}));
    }

    @Test(order = 8)
    void reversedRun() {
        assertEquals(9, Streaks.longest(new int[] {9, 8, 7, 6, 5, 4, 3, 2, 1}));
    }

    @Test(order = 9)
    void valuesAtTheEdgesOfTheRange() {
        int[] nums = {1_000_000_000, 999_999_999, -1_000_000_000, -999_999_999, -999_999_998};
        assertEquals(3, Streaks.longest(nums));
    }

    private static int[] shuffledRun(int length, int copies) {
        Random random = new Random(3);
        int[] nums = new int[length * copies];
        for (int i = 0; i < nums.length; i++) {
            nums[i] = i % length;
        }
        for (int i = nums.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            int t = nums[i];
            nums[i] = nums[j];
            nums[j] = t;
        }
        return nums;
    }

    @Test(order = 10, timeoutMs = 2000)
    void largeSingleRun() {
        assertEquals(100_000, Streaks.longest(shuffledRun(100_000, 1)));
    }

    @Test(order = 11, timeoutMs = 2000)
    void largeRunWithEveryValueDoubled() {
        assertEquals(50_000, Streaks.longest(shuffledRun(50_000, 2)));
    }
}
