import static kit.Check.assertEquals;
import static kit.Check.assertTrue;

import java.util.Arrays;
import java.util.Random;
import kit.Test;

public class Phase1Tests {

    /** Checks the answer and that nums is exactly as it was before the call. */
    private static void expect(int[] nums, int duplicate) {
        int[] before = nums.clone();
        int got = FindDuplicate.find(nums);
        assertTrue(Arrays.equals(before, nums), "nums was changed; read it without writing to it");
        assertEquals(duplicate, got);
    }

    /** n + 1 values: 1..n in a seeded shuffle, with the value at index n - 1 copied to index n. */
    private static int[] shuffledWithLastTwoEqual(int n, long seed) {
        int[] nums = new int[n + 1];
        for (int i = 0; i < n; i++) {
            nums[i] = i + 1;
        }
        Random random = new Random(seed);
        for (int i = n - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            int t = nums[i];
            nums[i] = nums[j];
            nums[j] = t;
        }
        nums[n] = nums[n - 1];
        return nums;
    }

    @Test(order = 1)
    void sample() {
        expect(new int[] {3, 5, 4, 1, 2, 4}, 4);
    }

    @Test(order = 2)
    void valueRepeatedSeveralTimes() {
        expect(new int[] {2, 6, 2, 1, 2, 5, 2}, 2);
        expect(new int[] {3, 1, 3, 3, 3}, 3);
    }

    @Test(order = 3)
    void smallestInput() {
        expect(new int[] {1, 1}, 1);
    }

    @Test(order = 4)
    void duplicateIsTheLargestValue() {
        expect(new int[] {6, 2, 6, 1, 5, 3, 4}, 6);
    }

    @Test(order = 5)
    void duplicateIsOne() {
        expect(new int[] {5, 1, 3, 2, 1, 4}, 1);
    }

    @Test(order = 6)
    void adjacentCopies() {
        expect(new int[] {2, 3, 3, 1}, 3);
    }

    @Test(order = 7)
    void arrayIsLeftUnchanged() {
        int[] nums = {7, 3, 1, 7, 6, 2, 5, 4};
        expect(nums, 7);
        assertEquals("[7, 3, 1, 7, 6, 2, 5, 4]", Arrays.toString(nums));
    }

    @Test(order = 8)
    void halfTheArrayIsOneValue() {
        int n = 200_000;
        int[] nums = shuffledWithLastTwoEqual(n, 41);
        int value = nums[0];
        for (int i = 0; i <= n; i += 2) {
            nums[i] = value; // values at even indexes are lost, value takes their place
        }
        expect(nums, value);
    }

    @Test(order = 9, timeoutMs = 250)
    void largeArrayWithTheCopiesAtTheEnd() {
        int n = 200_000;
        int[] nums = shuffledWithLastTwoEqual(n, 40);
        expect(nums, nums[n]);
    }
}
