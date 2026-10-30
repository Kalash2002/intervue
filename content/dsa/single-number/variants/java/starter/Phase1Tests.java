import static kit.Check.assertEquals;

import java.util.Arrays;
import java.util.Random;
import kit.Test;

public class Phase1Tests {

    private static final int LIMIT = 30_000; // values lie in [-LIMIT, LIMIT]

    private static void expect(int want, int[] nums) {
        String call = nums.length <= 20 ? "find(" + Arrays.toString(nums) + ")" : "find(an array of " + nums.length + " values)";
        assertEquals(want, LoneValue.find(nums), call);
    }

    /** Every allowed value once, in order. */
    private static int[] everyAllowedValue() {
        int[] pool = new int[2 * LIMIT + 1];
        for (int i = 0; i < pool.length; i++) {
            pool[i] = i - LIMIT;
        }
        return pool;
    }

    /** Moves count distinct values of pool, picked at random, to its front. */
    private static void pickDistinct(int[] pool, int count, Random random) {
        for (int j = 0; j < count; j++) {
            swap(pool, j, j + random.nextInt(pool.length - j));
        }
    }

    /** Shuffles a[from..to). */
    private static void shuffle(int[] a, int from, int to, Random random) {
        for (int j = to - 1; j > from; j--) {
            swap(a, j, from + random.nextInt(j - from + 1));
        }
    }

    private static void swap(int[] a, int i, int j) {
        int t = a[i];
        a[i] = a[j];
        a[j] = t;
    }

    @Test(order = 1)
    void sample() {
        expect(5, new int[] {12, 5, 10, 12, 10});
        expect(-7, new int[] {0, 40, -7, 3, 40, 0, 3});
        expect(-30000, new int[] {-30000});
    }

    @Test(order = 2)
    void oneValue() {
        expect(30000, new int[] {30000});
        expect(742, new int[] {742});
        expect(-58, new int[] {-58});
    }

    @Test(order = 3)
    void copiesFarApart() {
        expect(27, new int[] {8, 14, -3, 27, -3, 14, 8});
        expect(19, new int[] {19, 6, 33, 50, 6, 33, 50});
        expect(16, new int[] {70, 45, 91, 70, 45, 91, 16});
        expect(64, new int[] {35, 81, 64, 23, 35, 81, 23});
    }

    @Test(order = 4)
    void zeroAndNegativeValues() {
        expect(0, new int[] {-9, 0, 31, -9, 31});
        expect(-300, new int[] {-1000, -20, -1000, -300, -20});
        expect(17, new int[] {-6, 6, 17, -6, 6}); // 6 and -6 are different values
        expect(-6, new int[] {6, 17, -6, 6, 17});
    }

    @Test(order = 5)
    void valuesAtTheLimits() {
        expect(-30000, new int[] {30000, -30000, 30000});
        expect(30000, new int[] {-30000, 29999, 30000, -30000, 29999});
    }

    @Test(order = 6)
    void theLoneValueIsTheSmallestOrTheLargest() {
        // sorted, the lone value comes first or last, with no partner on one side
        expect(60, new int[] {60, 13, 27, 13, 27});
        expect(-45, new int[] {13, 27, -45, 27, 13});
        expect(88, new int[] {9, 0, 9, -14, 88, 0, -14});
    }

    @Test(order = 7)
    void randomArrays() {
        Random random = new Random(1601);
        int[] pool = everyAllowedValue();
        for (int round = 0; round < 500; round++) {
            int pairs = random.nextInt(301);
            pickDistinct(pool, pairs + 1, random);
            int[] nums = new int[2 * pairs + 1];
            System.arraycopy(pool, 0, nums, 0, pairs);
            System.arraycopy(pool, 0, nums, pairs, pairs);
            nums[2 * pairs] = pool[pairs];
            shuffle(nums, 0, nums.length, random);
            expect(pool[pairs], nums);
        }
    }

    @Test(order = 8, timeoutMs = 400)
    void largeArraysWithTheLoneValueInTheMiddle() {
        Random random = new Random(1607);
        int[] pool = everyAllowedValue();
        int pairs = 14_999;
        int n = 2 * pairs + 1; // 29,999 values
        for (int round = 0; round < 50; round++) {
            pickDistinct(pool, pairs + 1, random);
            // the first copies in one random order, then the second copies in another, so each
            // copy is far from its partner, and the lone value somewhere in the middle third
            int[] copies = new int[2 * pairs];
            System.arraycopy(pool, 0, copies, 0, pairs);
            System.arraycopy(pool, 0, copies, pairs, pairs);
            shuffle(copies, pairs, 2 * pairs, random);
            int at = n / 3 + random.nextInt(n / 3);
            int[] nums = new int[n];
            System.arraycopy(copies, 0, nums, 0, at);
            nums[at] = pool[pairs];
            System.arraycopy(copies, at, nums, at + 1, 2 * pairs - at);
            expect(pool[pairs], nums);
        }
    }
}
