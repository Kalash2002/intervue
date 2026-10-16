import static kit.Check.assertEquals;

import java.util.Random;
import kit.Test;

public class Phase1Tests {

    @Test(order = 1)
    void repeatInTheMiddle() {
        assertEquals(true, ContainsDuplicate.hasDuplicate(new int[] {7, 2, 9, 7}));
    }

    @Test(order = 2)
    void allDistinct() {
        assertEquals(false, ContainsDuplicate.hasDuplicate(new int[] {14, -3, 0, 8}));
    }

    @Test(order = 3)
    void emptyAndSingle() {
        assertEquals(false, ContainsDuplicate.hasDuplicate(new int[] {}));
        assertEquals(false, ContainsDuplicate.hasDuplicate(new int[] {42}));
    }

    @Test(order = 4)
    void adjacentRepeat() {
        assertEquals(true, ContainsDuplicate.hasDuplicate(new int[] {5, 8, 8, 1}));
    }

    @Test(order = 5)
    void firstAndLastEqual() {
        assertEquals(true, ContainsDuplicate.hasDuplicate(new int[] {3, 10, 20, 30, 3}));
    }

    @Test(order = 6)
    void negativesAndZero() {
        assertEquals(false, ContainsDuplicate.hasDuplicate(new int[] {-1, 1, 0, -2, 2}));
        assertEquals(true, ContainsDuplicate.hasDuplicate(new int[] {-6, 0, 4, -6}));
    }

    @Test(order = 7)
    void extremeValues() {
        assertEquals(false, ContainsDuplicate.hasDuplicate(new int[] {Integer.MIN_VALUE, Integer.MAX_VALUE, 0}));
        assertEquals(true, ContainsDuplicate.hasDuplicate(new int[] {Integer.MAX_VALUE, 1, Integer.MAX_VALUE}));
    }

    @Test(order = 8)
    void everyValueRepeats() {
        assertEquals(true, ContainsDuplicate.hasDuplicate(new int[] {2, 2, 2, 2}));
    }

    @Test(order = 9, timeoutMs = 2000)
    void largeDistinctArray() {
        int n = 300_000;
        int[] nums = new int[n];
        for (int i = 0; i < n; i++) {
            nums[i] = i * 3 - 100_000;
        }
        Random random = new Random(11);
        for (int i = n - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            int swap = nums[i];
            nums[i] = nums[j];
            nums[j] = swap;
        }
        assertEquals(false, ContainsDuplicate.hasDuplicate(nums));
    }
}
