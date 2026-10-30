import static kit.Check.assertEquals;

import kit.Test;

public class Phase1Tests {

    // Built once per test JVM: 0, 3, 6, ... shifted down by 1,500,000, so negatives are included.
    private static final int N = 1_000_000;
    private static final int[] STEPS_OF_THREE = new int[N];

    static {
        for (int i = 0; i < N; i++) {
            STEPS_OF_THREE[i] = 3 * i - 1_500_000;
        }
    }

    @Test(order = 1)
    void sample() {
        int[] nums = {-7, -2, 3, 11, 25, 40};
        assertEquals(3, SortedLookup.indexOf(nums, 11));
        assertEquals(-1, SortedLookup.indexOf(nums, 4));
    }

    @Test(order = 2)
    void emptyArray() {
        assertEquals(-1, SortedLookup.indexOf(new int[] {}, 5));
    }

    @Test(order = 3)
    void singleElement() {
        int[] one = {-12};
        assertEquals(0, SortedLookup.indexOf(one, -12));
        assertEquals(-1, SortedLookup.indexOf(one, -13));
        assertEquals(-1, SortedLookup.indexOf(one, -11));
    }

    @Test(order = 4)
    void firstAndLastPositions() {
        int[] nums = {5, 8, 13, 21, 34, 55, 89};
        assertEquals(0, SortedLookup.indexOf(nums, 5));
        assertEquals(6, SortedLookup.indexOf(nums, 89));
        assertEquals(-1, SortedLookup.indexOf(nums, 4));
        assertEquals(-1, SortedLookup.indexOf(nums, 90));
    }

    @Test(order = 5)
    void twoElements() {
        int[] nums = {30, 31};
        assertEquals(0, SortedLookup.indexOf(nums, 30));
        assertEquals(1, SortedLookup.indexOf(nums, 31));
        assertEquals(-1, SortedLookup.indexOf(nums, 29));
        assertEquals(-1, SortedLookup.indexOf(nums, 32));
    }

    @Test(order = 6)
    void extremeIntValues() {
        // A comparison written as nums[middle] - target overflows here and walks the wrong way.
        int[] nums = {Integer.MIN_VALUE, 0, Integer.MAX_VALUE};
        assertEquals(0, SortedLookup.indexOf(nums, Integer.MIN_VALUE));
        assertEquals(2, SortedLookup.indexOf(nums, Integer.MAX_VALUE));
        assertEquals(1, SortedLookup.indexOf(nums, 0));
        assertEquals(-1, SortedLookup.indexOf(nums, -1));
    }

    @Test(order = 7)
    void everyValueAndEveryGap() {
        int[] nums = {2, 4, 6, 8, 10, 12, 14, 16};
        for (int target = 0; target <= 18; target++) {
            // even targets 2..16 sit at index target / 2 - 1; everything else is absent
            int expected = target % 2 == 0 && target >= 2 && target <= 16 ? target / 2 - 1 : -1;
            assertEquals(expected, SortedLookup.indexOf(nums, target), "target " + target);
        }
    }

    @Test(order = 8, timeoutMs = 600)
    void sixtyThousandLookupsInAMillionValues() {
        for (int k = 0; k < 60_000; k++) {
            int offset = (int) ((k * 7919L) % (3L * N)); // 0 to 2,999,999 above the smallest value
            int target = offset - 1_500_000;
            int expected = offset % 3 == 0 ? offset / 3 : -1;
            assertEquals(expected, SortedLookup.indexOf(STEPS_OF_THREE, target), "target " + target);
        }
    }
}
