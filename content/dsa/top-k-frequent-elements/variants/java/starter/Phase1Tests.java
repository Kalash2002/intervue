import static kit.Check.assertEquals;

import java.util.Arrays;
import java.util.Random;
import kit.Test;

public class Phase1Tests {

    private static void expect(int[] nums, int k, int... expected) {
        int[] got = TopKFrequent.topK(nums, k);
        assertEquals(k, got.length, "return exactly k values");
        int[] sorted = got.clone();
        Arrays.sort(sorted);
        int[] want = expected.clone();
        Arrays.sort(want);
        assertEquals(Arrays.toString(want), Arrays.toString(sorted));
    }

    @Test(order = 1)
    void sample() {
        expect(new int[] {5, 5, 5, 2, 2, 9}, 2, 5, 2);
    }

    @Test(order = 2)
    void singleElement() {
        expect(new int[] {8}, 1, 8);
    }

    @Test(order = 3)
    void negativeAndZeroValues() {
        expect(new int[] {-1, -1, 4, 4, 4, 0, 0, 0, 0}, 1, 0);
        expect(new int[] {-1, -1, 4, 4, 4, 0, 0, 0, 0}, 2, 0, 4);
    }

    @Test(order = 4)
    void kEqualsNumberOfDistinctValues() {
        expect(new int[] {1, 2, 2, 3, 3, 3}, 3, 1, 2, 3);
    }

    @Test(order = 5)
    void allValuesEqual() {
        expect(new int[] {6, 6, 6, 6}, 1, 6);
    }

    @Test(order = 6)
    void topValueAtTheEnd() {
        expect(new int[] {1, 2, 3, 4, 9, 9, 9, 9, 3, 3}, 2, 9, 3);
    }

    @Test(order = 7)
    void largeValues() {
        expect(new int[] {Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE}, 1, Integer.MAX_VALUE);
    }

    @Test(order = 8)
    void valuesSeparatedByManySingles() {
        int[] nums = new int[40];
        for (int i = 0; i < 30; i++) {
            nums[i] = 100 + i; // thirty values that appear once
        }
        for (int i = 30; i < 40; i++) {
            nums[i] = i < 36 ? 7 : 8; // 7 six times, 8 four times
        }
        expect(nums, 2, 7, 8);
    }

    @Test(order = 9, timeoutMs = 2000)
    void largeArray() {
        int[] heavyCounts = {5000, 4000, 3000, 2000, 1000};
        int[] heavyValues = {-77, 123456, 0, 31, 999_999};
        int n = 200_000;
        int[] nums = new int[n];
        int at = 0;
        for (int h = 0; h < heavyCounts.length; h++) {
            for (int c = 0; c < heavyCounts[h]; c++) {
                nums[at++] = heavyValues[h];
            }
        }
        int single = 1_000_000;
        while (at < n) {
            nums[at++] = single++; // every other value appears once
        }
        Random random = new Random(5);
        for (int i = n - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            int swap = nums[i];
            nums[i] = nums[j];
            nums[j] = swap;
        }
        expect(nums, 5, heavyValues);
        expect(nums, 3, -77, 123456, 0);
    }
}
