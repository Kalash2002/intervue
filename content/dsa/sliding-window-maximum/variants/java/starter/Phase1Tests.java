import static kit.Check.assertEquals;

import java.util.Arrays;
import java.util.Random;
import kit.Test;

public class Phase1Tests {

    private static void expect(int[] nums, int k, int... expected) {
        assertEquals(Arrays.toString(expected), Arrays.toString(WindowMax.maxima(nums, k)));
    }

    @Test(order = 1)
    void sample() {
        expect(new int[] {2, 9, 4, 1, 6, 3}, 3, 9, 9, 6, 6);
    }

    @Test(order = 2)
    void windowOfOne() {
        expect(new int[] {-3, -1, -2}, 1, -3, -1, -2);
    }

    @Test(order = 3)
    void windowAsWideAsTheArray() {
        expect(new int[] {4, 8, 1, 7}, 4, 8);
        expect(new int[] {6}, 1, 6);
    }

    @Test(order = 4)
    void allValuesEqual() {
        expect(new int[] {5, 5, 5}, 2, 5, 5);
    }

    @Test(order = 5)
    void increasingValues() {
        expect(new int[] {1, 2, 3, 4, 5}, 3, 3, 4, 5);
    }

    @Test(order = 6)
    void decreasingValues() {
        expect(new int[] {9, 7, 5, 3, 1}, 2, 9, 7, 5, 3);
    }

    @Test(order = 7)
    void negativeValues() {
        expect(new int[] {-8, -3, -6, -9, -2}, 3, -3, -3, -2);
    }

    @Test(order = 8)
    void maximumLeavesExactlyWhenItExpires() {
        expect(new int[] {10, 1, 2, 3, 4}, 3, 10, 3, 4);
    }

    @Test(order = 9)
    void extremeValues() {
        expect(new int[] {Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE}, 2,
                Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    @Test(order = 10, timeoutMs = 2000)
    void largeArray() {
        int n = 1_000_000;
        int k = 500_000;
        Random random = new Random(21);
        int[] nums = new int[n];
        for (int i = 0; i < n; i++) {
            nums[i] = random.nextInt();
        }
        // The expected answer from prefix and suffix maxima inside blocks of width k.
        int[] prefix = new int[n];
        int[] suffix = new int[n];
        for (int i = 0; i < n; i++) {
            prefix[i] = i % k == 0 ? nums[i] : Math.max(prefix[i - 1], nums[i]);
        }
        for (int i = n - 1; i >= 0; i--) {
            suffix[i] = (i == n - 1 || (i + 1) % k == 0) ? nums[i] : Math.max(suffix[i + 1], nums[i]);
        }
        int[] got = WindowMax.maxima(nums, k);
        assertEquals(n - k + 1, got.length);
        for (int i = 0; i < got.length; i++) {
            int want = Math.max(suffix[i], prefix[i + k - 1]);
            if (got[i] != want) {
                assertEquals(want, got[i], "window starting at " + i);
            }
        }
    }
}
