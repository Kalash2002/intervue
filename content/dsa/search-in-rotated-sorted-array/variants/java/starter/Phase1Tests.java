import static kit.Check.assertEquals;

import kit.Test;

public class Phase1Tests {

    // Built once per test JVM: eight arrays of 2^19 values, array k holding 2j - 500,000 + 7k
    // for j = 0 .. n - 1, rotated left by SHIFTS[k] (sorted index j lands at (j - shift) mod n).
    private static final int N = 1 << 19;
    private static final int[] SHIFTS = {0, 1, N / 2, N - 1, 12_345, 300_000, 77_777, 500_001};
    private static final int[][] BIG = new int[SHIFTS.length][N];

    static {
        for (int k = 0; k < SHIFTS.length; k++) {
            for (int i = 0; i < N; i++) {
                BIG[k][i] = 2 * ((i + SHIFTS[k]) % N) - 500_000 + 7 * k;
            }
        }
    }

    /** sorted rotated left by shift: element i is sorted[(i + shift) % n]. */
    private static int[] rotate(int[] sorted, int shift) {
        int[] out = new int[sorted.length];
        for (int i = 0; i < sorted.length; i++) {
            out[i] = sorted[(i + shift) % sorted.length];
        }
        return out;
    }

    @Test(order = 1)
    void sample() {
        int[] nums = {27, 33, 40, 51, 2, 9, 15};
        assertEquals(5, RotatedSearch.indexOf(nums, 9));
        assertEquals(-1, RotatedSearch.indexOf(nums, 30));
    }

    @Test(order = 2)
    void bothRunsAndTheirEnds() {
        int[] nums = {27, 33, 40, 51, 2, 9, 15};
        assertEquals(0, RotatedSearch.indexOf(nums, 27));
        assertEquals(3, RotatedSearch.indexOf(nums, 51));
        assertEquals(4, RotatedSearch.indexOf(nums, 2));
        assertEquals(6, RotatedSearch.indexOf(nums, 15));
        assertEquals(-1, RotatedSearch.indexOf(nums, 20)); // between the runs' values
        assertEquals(-1, RotatedSearch.indexOf(nums, 1));
        assertEquals(-1, RotatedSearch.indexOf(nums, 60));
    }

    @Test(order = 3)
    void notRotated() {
        int[] nums = {-8, -1, 4, 10, 23};
        assertEquals(0, RotatedSearch.indexOf(nums, -8));
        assertEquals(3, RotatedSearch.indexOf(nums, 10));
        assertEquals(-1, RotatedSearch.indexOf(nums, 5));
    }

    @Test(order = 4)
    void singleElement() {
        assertEquals(0, RotatedSearch.indexOf(new int[] {6}, 6));
        assertEquals(-1, RotatedSearch.indexOf(new int[] {6}, 7));
    }

    @Test(order = 5)
    void twoElements() {
        // The middle equals low here: a strict nums[low] < nums[middle] picks the wrong half.
        assertEquals(1, RotatedSearch.indexOf(new int[] {9, 3}, 3));
        assertEquals(0, RotatedSearch.indexOf(new int[] {9, 3}, 9));
        assertEquals(-1, RotatedSearch.indexOf(new int[] {9, 3}, 5));
        assertEquals(1, RotatedSearch.indexOf(new int[] {3, 9}, 9));
        assertEquals(0, RotatedSearch.indexOf(new int[] {3, 9}, 3));
    }

    @Test(order = 6)
    void everyRotationEveryTarget() {
        int[] sorted = {1, 4, 6, 10, 13, 17, 22, 30};
        for (int shift = 0; shift < sorted.length; shift++) {
            int[] nums = rotate(sorted, shift);
            for (int j = 0; j < sorted.length; j++) {
                int expected = (j - shift + sorted.length) % sorted.length;
                assertEquals(expected, RotatedSearch.indexOf(nums, sorted[j]), "shift " + shift + ", target " + sorted[j]);
                // 2, 5, 7, 11, 14, 18, 23 and 31 are all absent
                assertEquals(-1, RotatedSearch.indexOf(nums, sorted[j] + 1), "shift " + shift + ", target " + (sorted[j] + 1));
            }
        }
    }

    @Test(order = 7)
    void extremeIntValues() {
        int[] nums = {0, Integer.MAX_VALUE, Integer.MIN_VALUE, -1};
        assertEquals(2, RotatedSearch.indexOf(nums, Integer.MIN_VALUE));
        assertEquals(1, RotatedSearch.indexOf(nums, Integer.MAX_VALUE));
        assertEquals(3, RotatedSearch.indexOf(nums, -1));
        assertEquals(-1, RotatedSearch.indexOf(nums, 1));
    }

    @Test(order = 8, timeoutMs = 600)
    void fortyThousandLookupsInHalfMillionValueArrays() {
        for (int call = 0; call < 40_000; call++) {
            int k = call % SHIFTS.length;
            int j = (int) ((call * 7919L) % N);
            int value = 2 * j - 500_000 + 7 * k;
            if (call % 4 == 3) {
                // An odd offset from a value of array k never occurs in it.
                assertEquals(-1, RotatedSearch.indexOf(BIG[k], value + 1), "array " + k + ", absent");
            } else {
                assertEquals((j - SHIFTS[k] + N) % N, RotatedSearch.indexOf(BIG[k], value), "array " + k + ", value " + value);
            }
        }
    }
}
