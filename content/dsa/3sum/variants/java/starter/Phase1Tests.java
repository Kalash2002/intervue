import static kit.Check.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import kit.Test;

public class Phase1Tests {

    /** Sorts each triple, then the list, and prints it, so any order is accepted. */
    private static String normal(List<List<Integer>> triples) {
        List<String> parts = new ArrayList<>();
        for (List<Integer> t : triples) {
            List<Integer> sorted = new ArrayList<>(t);
            sorted.sort(null);
            parts.add(sorted.toString());
        }
        parts.sort(null);
        return parts.toString();
    }

    private static void expect(int[] nums, String want) {
        int[] copy = nums.clone();
        assertEquals(want, normal(Triples.zeroSum(nums)));
        assertEquals(Arrays.toString(copy), Arrays.toString(nums), "nums must not be changed");
    }

    @Test(order = 1)
    void sample() {
        expect(new int[] {-4, 1, 3, 0, 2, -1, 4}, "[[-1, 0, 1], [-4, 0, 4], [-4, 1, 3]]");
    }

    @Test(order = 2)
    void fewerThanThreeValues() {
        expect(new int[] {}, "[]");
        expect(new int[] {0}, "[]");
        expect(new int[] {0, 0}, "[]");
    }

    @Test(order = 3)
    void allZeros() {
        expect(new int[] {0, 0, 0, 0, 0}, "[[0, 0, 0]]");
    }

    @Test(order = 4)
    void repeatedValuesGiveTheTripleOnce() {
        expect(new int[] {-2, -2, 0, 0, 2, 2}, "[[-2, 0, 2]]");
    }

    @Test(order = 5)
    void noTriple() {
        expect(new int[] {1, 2, 3, 4}, "[]");
        expect(new int[] {-1, -2, -3}, "[]");
    }

    @Test(order = 6)
    void tripleWithTwoEqualValues() {
        expect(new int[] {-1, -1, 2}, "[[-1, -1, 2]]");
    }

    @Test(order = 7)
    void singleZeroIsNotUsedThreeTimes() {
        expect(new int[] {0, 1, -1, 5}, "[[-1, 0, 1]]");
        expect(new int[] {0}, "[]");
    }

    @Test(order = 8)
    void mixedWithRepeatedNegatives() {
        expect(new int[] {-6, -4, 2, 10, -6, 4, 0}, "[[-4, 0, 4], [-6, -4, 10], [-6, 2, 4]]");
    }

    @Test(order = 9, timeoutMs = 1000)
    void largeWithOneTriple() {
        Random random = new Random(21);
        int[] nums = new int[5_000];
        for (int i = 0; i < 4_997; i++) {
            int k = random.nextInt(39_999) - 19_999; // k in -19999..19999
            if (k == 0) {
                k = 1;
            }
            nums[i] = 5 * k + 1; // always 1 more than a multiple of 5, never 1 itself
        }
        nums[4_997] = -3;
        nums[4_998] = 1;
        nums[4_999] = 2;
        for (int i = nums.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            int t = nums[i];
            nums[i] = nums[j];
            nums[j] = t;
        }
        expect(nums, "[[-3, 1, 2]]");
    }

    @Test(order = 10, timeoutMs = 1000)
    void largeAllZeros() {
        expect(new int[5_000], "[[0, 0, 0]]");
    }
}
