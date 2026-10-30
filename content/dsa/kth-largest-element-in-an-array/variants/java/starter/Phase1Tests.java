import static kit.Check.assertEquals;

import java.util.Random;
import kit.Test;

public class Phase1Tests {

    private static final int N = 100_000;

    @Test(order = 1)
    void sample() {
        assertEquals(19, KthElement.kthLargest(new int[] {12, 5, 30, 7, 19}, 2));
    }

    @Test(order = 2)
    void repeatsCountSeparately() {
        assertEquals(8, KthElement.kthLargest(new int[] {8, 3, 8, 8, 1}, 3));
        assertEquals(3, KthElement.kthLargest(new int[] {8, 3, 8, 8, 1}, 4));
    }

    @Test(order = 3)
    void kEqualsLengthGivesTheSmallest() {
        assertEquals(-2, KthElement.kthLargest(new int[] {4, -2, 9}, 3));
    }

    @Test(order = 4)
    void kOneGivesTheLargest() {
        assertEquals(-3, KthElement.kthLargest(new int[] {-15, -3, -40}, 1));
    }

    @Test(order = 5)
    void singleValue() {
        assertEquals(-7, KthElement.kthLargest(new int[] {-7}, 1));
    }

    @Test(order = 6)
    void valuesAtTheLimits() {
        assertEquals(1_000_000_000, KthElement.kthLargest(new int[] {1_000_000_000, -1_000_000_000, 5}, 1));
        assertEquals(-1_000_000_000, KthElement.kthLargest(new int[] {1_000_000_000, -1_000_000_000, 5}, 3));
    }

    @Test(order = 7)
    void allEqualSmall() {
        assertEquals(6, KthElement.kthLargest(new int[] {6, 6, 6, 6}, 2));
    }

    @Test(order = 8, timeoutMs = 1000)
    void largeShuffled() {
        int[] nums = new int[N];
        for (int i = 0; i < N; i++) {
            nums[i] = 2 * i - 100_000; // the k-th largest is 2 * (N - k) - 100,000
        }
        Random random = new Random(8);
        for (int i = N - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            int swap = nums[i];
            nums[i] = nums[j];
            nums[j] = swap;
        }
        assertEquals(99_998, KthElement.kthLargest(nums, 1));
        assertEquals(37_170, KthElement.kthLargest(nums, 31_415));
        assertEquals(0, KthElement.kthLargest(nums, 50_000));
        assertEquals(-100_000, KthElement.kthLargest(nums, N));
    }

    @Test(order = 9, timeoutMs = 1000)
    void largeAllEqual() {
        int[] nums = new int[N];
        java.util.Arrays.fill(nums, 424_242);
        assertEquals(424_242, KthElement.kthLargest(nums, 1));
        assertEquals(424_242, KthElement.kthLargest(nums, 50_000));
        assertEquals(424_242, KthElement.kthLargest(nums, N));
    }

    @Test(order = 10, timeoutMs = 1000)
    void largeSortedAndRepeating() {
        int[] sorted = new int[N];
        for (int i = 0; i < N; i++) {
            sorted[i] = i - 50_000; // -50,000 .. 49,999: the k-th largest is 50,000 - k
        }
        assertEquals(49_999, KthElement.kthLargest(sorted, 1));
        assertEquals(-27_777, KthElement.kthLargest(sorted, 77_777));
        int[] cycling = new int[N];
        for (int i = 0; i < N; i++) {
            cycling[i] = i % 1000; // each of 0..999 a hundred times: the k-th largest is 999 - (k - 1) / 100
        }
        assertEquals(999, KthElement.kthLargest(cycling, 100));
        assertEquals(998, KthElement.kthLargest(cycling, 101));
        assertEquals(444, KthElement.kthLargest(cycling, 55_555));
    }
}
