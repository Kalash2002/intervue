import static kit.Check.assertEquals;

import java.util.Arrays;
import java.util.Random;
import kit.Test;

public class Phase1Tests {

    private static void expect(int want, int... nums) {
        String input = nums.length <= 40 ? Arrays.toString(nums) : nums.length + " values";
        assertEquals(want, Rising.longest(nums.clone()), "nums = " + input);
    }

    @Test(order = 1)
    void sample() {
        expect(4, 4, 9, 5, 2, 6, 3, 8);
        expect(4, -3, 10, -1, 0, 0, 2);
        expect(1, 12, 8, 5, 1);
    }

    @Test(order = 2)
    void equalValuesDoNotRise() {
        expect(3, 2, 2, 4, 4, 8, 8);
        expect(2, -1, -1, -1, 0);
        expect(1, 3, 3);
    }

    @Test(order = 3)
    void thePickedValuesNeedNotBeNeighbours() {
        expect(3, 3, 1, 4, 2, 5); // no two neighbours rise twice in a row
        expect(4, 10, 1, 11, 2, 12, 3, 13);
    }

    @Test(order = 4)
    void takingTheNextLargerValueAtOnceCanBeWrong() {
        expect(4, 2, 9, 3, 4, 5); // 2, 3, 4, 5 skips the 9
        expect(4, 9, 1, 2, 3, 10); // starting at the first value gives only 9, 10
        expect(3, 5, 6, 7, 1, 2); // starting at the smallest value gives only 1, 2
    }

    @Test(order = 5)
    void aSingleValue() {
        expect(1, 0);
        expect(1, -1_000_000_000);
    }

    @Test(order = 6)
    void valuesAtTheLimits() {
        expect(3, 1_000_000_000, -1_000_000_000, 0, 999_999_999);
        expect(2, -1_000_000_000, 1_000_000_000);
    }

    @Test(order = 7)
    void randomShortArrays() {
        Random random = new Random(2109);
        for (int round = 0; round < 300; round++) {
            int[] nums = new int[1 + random.nextInt(12)];
            for (int i = 0; i < nums.length; i++) {
                nums[i] = random.nextInt(8);
            }
            expect(everySubset(nums), nums);
        }
    }

    /** Tries every set of positions and keeps the largest one whose values strictly rise. */
    private static int everySubset(int[] nums) {
        int best = 0;
        for (int set = 1; set < 1 << nums.length; set++) {
            int previous = Integer.MIN_VALUE;
            boolean rising = true;
            for (int i = 0; i < nums.length && rising; i++) {
                if ((set >> i & 1) == 1) {
                    rising = previous == Integer.MIN_VALUE || nums[i] > previous;
                    previous = nums[i];
                }
            }
            if (rising) {
                best = Math.max(best, Integer.bitCount(set));
            }
        }
        return best;
    }

    @Test(order = 8, timeoutMs = 300)
    void manyFallingBlocksThatRiseFromBlockToBlock() {
        // 400 blocks of 500 values; each block falls, and every value of a block is above every
        // value of the block before, so one value per block can be picked and no more
        int[] nums = new int[200_000];
        for (int block = 0; block < 400; block++) {
            for (int k = 0; k < 500; k++) {
                nums[500 * block + k] = 1000 * block + 499 - k;
            }
        }
        expect(400, nums);
    }

    @Test(order = 9, timeoutMs = 300)
    void aLongRiseInEqualPairs() {
        // -10^9, -10^9, -10^9 + 1, -10^9 + 1, ...: one value of each pair
        int[] nums = new int[200_000];
        for (int i = 0; i < nums.length; i++) {
            nums[i] = -1_000_000_000 + i / 2;
        }
        expect(100_000, nums);
    }

    @Test(order = 10, timeoutMs = 300)
    void aLongRiseThenValuesThatFallBetweenIt() {
        // the evens 0, 2, ..., 199998 rise; then the odds 199999, 199997, ..., 1 fall, so a
        // rising pick takes at most one of them: all the evens and then 199999
        int[] nums = new int[200_000];
        for (int i = 0; i < 100_000; i++) {
            nums[i] = 2 * i;
            nums[100_000 + i] = 199_999 - 2 * i;
        }
        expect(100_001, nums);
    }

    @Test(order = 11, timeoutMs = 300)
    void aLongRiseThenManyOnes() {
        // the evens 0, 2, ..., 199998 rise, then 100000 ones follow; a 1 can only follow the 0,
        // and the ones are equal, so the evens alone are the longest pick
        int[] nums = new int[200_000];
        for (int i = 0; i < 100_000; i++) {
            nums[i] = 2 * i;
            nums[100_000 + i] = 1;
        }
        expect(100_000, nums);
    }
}
