import static kit.Check.assertEquals;

import java.util.Arrays;
import kit.Test;

public class Phase1Tests {

    private static void expect(int[] nums, int target, int i, int j) {
        int[] got = TwoSum.indices(nums, target);
        assertEquals(2, got.length, "return an array of two positions");
        int[] sorted = got.clone();
        Arrays.sort(sorted);
        assertEquals(i, sorted[0]);
        assertEquals(j, sorted[1]);
    }

    @Test(order = 1)
    void basicPair() {
        expect(new int[] {4, 9, 2, 6}, 10, 0, 3);
    }

    @Test(order = 2)
    void equalValuesFormThePair() {
        expect(new int[] {3, 3}, 6, 0, 1);
    }

    @Test(order = 3)
    void negativeNumbers() {
        expect(new int[] {-5, 8, 2, 13}, 8, 0, 3);
        expect(new int[] {-4, -9, -1}, -13, 0, 1);
    }

    @Test(order = 4)
    void zeroPair() {
        expect(new int[] {0, 7, 0, 5}, 0, 0, 2);
    }

    @Test(order = 5)
    void aValueDoesNotPairWithItself() {
        expect(new int[] {5, 1, 9, 4}, 10, 1, 2);
    }

    @Test(order = 6)
    void pairAtTheVeryEnd() {
        expect(new int[] {10, 20, 30, 40, 7, 8}, 15, 4, 5);
    }

    @Test(order = 7)
    void largeValuesNearTheIntLimits() {
        expect(new int[] {900_000_000, -5, 1_000_000_000, 400_000_000}, 1_300_000_000, 0, 3);
    }

    @Test(order = 8, timeoutMs = 2000)
    void largeArray() {
        int n = 300_000;
        int[] nums = new int[n];
        for (int i = 0; i < n - 2; i++) {
            nums[i] = 4 * (i + 1); // multiples of 4: no two of them, or one plus 1 or 2, sum to 3
        }
        nums[n - 2] = 1;
        nums[n - 1] = 2;
        expect(nums, 3, n - 2, n - 1);
    }
}
