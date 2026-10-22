import static kit.Check.assertEquals;

import java.util.Arrays;
import java.util.Random;
import kit.Test;

public class Phase1Tests {

    private static void expect(long want, int[] nums, int target) {
        String shown = nums.length <= 20 ? Arrays.toString(nums) : nums.length + " numbers";
        assertEquals(want, Signs.ways(nums.clone(), target), "nums = " + shown + ", target = " + target);
    }

    @Test(order = 1)
    void sample() {
        expect(3, new int[] {4, 1, 3, 6, 2}, 2);
        expect(4, new int[] {0, 6, 0, 2}, -4);
        expect(0, new int[] {3, 8}, -12);
    }

    @Test(order = 2)
    void eachZeroCountsTwice() {
        // +7 -3 is the only way to make 4, and each 0 can take either sign
        expect(4, new int[] {7, 0, 3, 0}, 4);
    }

    @Test(order = 3)
    void aTargetOfTheWrongParityIsUnreachable() {
        // every total of 5, 2 and 6 is odd, like their sum 13
        expect(0, new int[] {5, 2, 6}, 4);
    }

    @Test(order = 4)
    void targetsAtAndBeyondTheEnds() {
        expect(1, new int[] {5, 2, 6}, 13);
        expect(1, new int[] {5, 2, 6}, -13);
        expect(0, new int[] {5, 2, 6}, 15);
        expect(0, new int[] {5, 2, 6}, -15);
    }

    @Test(order = 5)
    void aNegativeTargetMirrorsThePositiveOne() {
        // flipping every sign of the three choices of Example 1
        expect(3, new int[] {4, 1, 3, 6, 2}, -2);
    }

    @Test(order = 6)
    void twentyNumbersOfTwoValues() {
        // with a of the 3s and b of the 5s signed plus, 3(2a - 10) + 5(2b - 10) = 0 means 3a + 5b = 40:
        // (a, b) = (5, 5), (0, 8) or (10, 2), so 252 x 252 + 1 x 45 + 1 x 45 choices
        int[] nums = new int[20];
        for (int i = 0; i < nums.length; i++) {
            nums[i] = i % 2 == 0 ? 3 : 5;
        }
        expect(63_594, nums, 0);
    }

    @Test(order = 7)
    void randomShortRows() {
        Random random = new Random(509);
        for (int round = 0; round < 300; round++) {
            int[] nums = new int[1 + random.nextInt(12)];
            int sum = 0;
            for (int i = 0; i < nums.length; i++) {
                nums[i] = random.nextInt(10);
                sum += nums[i];
            }
            int target = random.nextInt(2 * sum + 5) - sum - 2;
            expect(everyChoice(nums, target), nums, target);
        }
    }

    /** Tries every choice of signs, one bit per number, and counts those that make target. */
    private static long everyChoice(int[] nums, int target) {
        long count = 0;
        for (int minus = 0; minus < 1 << nums.length; minus++) {
            int total = 0;
            for (int i = 0; i < nums.length; i++) {
                total += (minus >> i & 1) == 1 ? -nums[i] : nums[i];
            }
            if (total == target) {
                count++;
            }
        }
        return count;
    }

    private static int[] shuffled(int[] nums, long seed) {
        Random random = new Random(seed);
        for (int i = nums.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            int t = nums[i];
            nums[i] = nums[j];
            nums[j] = t;
        }
        return nums;
    }

    @Test(order = 8, timeoutMs = 200)
    void sixtyNumbersWithACountPastTheIntRange() {
        // 30 twos and 30 thirty-ones, mixed. With a twos and b thirty-ones signed plus, the total is
        // 4a + 62b - 990, which is 0 only for a = b = 15, so the count is (30 choose 15) squared.
        int[] nums = new int[60];
        for (int i = 0; i < nums.length; i++) {
            nums[i] = i < 30 ? 2 : 31;
        }
        expect(24_061_445_010_950_400L, shuffled(nums, 60), 0);
    }

    @Test(order = 9, timeoutMs = 200)
    void powersOfTwoAmongFiftyOneZeros() {
        // signed 1, 2, 4, ..., 256 reach every odd total from -511 to 511 in exactly one way,
        // and each of the 51 zeros doubles the count
        int[] nums = new int[60];
        for (int i = 0; i < 9; i++) {
            nums[i] = 1 << i;
        }
        expect(1L << 51, shuffled(nums, 51), 77);
    }
}
