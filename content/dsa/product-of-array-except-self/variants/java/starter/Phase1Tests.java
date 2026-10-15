import static kit.Check.assertEquals;
import static kit.Check.assertTrue;

import java.util.Arrays;
import java.util.Random;
import kit.Test;

public class Phase1Tests {

    private static void expect(int[] nums, int... want) {
        int[] copy = nums.clone();
        int[] got = Products.exceptSelf(nums);
        assertEquals(Arrays.toString(want), Arrays.toString(got));
        assertEquals(Arrays.toString(copy), Arrays.toString(nums), "nums must not be changed");
    }

    @Test(order = 1)
    void sample() {
        expect(new int[] {2, 5, 3}, 15, 6, 10);
    }

    @Test(order = 2)
    void twoElements() {
        expect(new int[] {-2, 7}, 7, -2);
        expect(new int[] {5, -3}, -3, 5);
    }

    @Test(order = 3)
    void negativeNumbers() {
        expect(new int[] {-1, 2, -3, 4}, -24, 12, -8, 6);
    }

    @Test(order = 4)
    void oneZero() {
        expect(new int[] {4, 0, 6}, 0, 24, 0);
        expect(new int[] {3, 0, 2, 5}, 0, 30, 0, 0);
    }

    @Test(order = 5)
    void twoZeros() {
        expect(new int[] {0, 4, 0, 7}, 0, 0, 0, 0);
    }

    @Test(order = 6)
    void allZeros() {
        expect(new int[] {0, 0}, 0, 0);
    }

    @Test(order = 7)
    void zeroAtAnEnd() {
        expect(new int[] {0, 2, 3}, 6, 0, 0);
        expect(new int[] {4, 5, 0}, 0, 0, 20);
    }

    @Test(order = 8)
    void allOnes() {
        expect(new int[] {1, 1, 1}, 1, 1, 1);
    }

    /** Only 1 and -1, plus twenty 2s, so every answer fits an int; the optional zero goes at zeroAt. */
    private static int[] largeInput(int zeroAt) {
        Random random = new Random(5);
        int[] nums = new int[200_000];
        for (int i = 0; i < nums.length; i++) {
            nums[i] = random.nextBoolean() ? 1 : -1;
        }
        for (int k = 0; k < 20; k++) {
            nums[1 + k * 9_000] = 2;
        }
        if (zeroAt >= 0) {
            nums[zeroAt] = 0;
        }
        return nums;
    }

    private static int[] oracle(int[] nums) {
        int zeros = 0;
        long product = 1; // product of the non-zero numbers; at most 2^20 in size
        for (int x : nums) {
            if (x == 0) {
                zeros++;
            } else {
                product *= x;
            }
        }
        int[] want = new int[nums.length];
        for (int i = 0; i < nums.length; i++) {
            if (zeros > 1) {
                want[i] = 0;
            } else if (zeros == 1) {
                want[i] = nums[i] == 0 ? (int) product : 0;
            } else {
                want[i] = (int) (product / nums[i]);
            }
        }
        return want;
    }

    private static void checkLarge(int[] nums) {
        int[] want = oracle(nums);
        int[] got = Products.exceptSelf(nums);
        assertTrue(Arrays.equals(want, got), "the products differ from the expected ones on the large input");
    }

    @Test(order = 9, timeoutMs = 1000)
    void largeWithoutZero() {
        checkLarge(largeInput(-1));
    }

    @Test(order = 10, timeoutMs = 1000)
    void largeWithOneZero() {
        checkLarge(largeInput(123_456));
    }
}
