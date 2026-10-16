import static kit.Check.assertEquals;

import kit.Test;

public class Phase1Tests {

    // Built once per test JVM: eight arrays of 2^19 values, array k holding 2i - 500,000 + 7k
    // rotated left by SHIFTS[k], so its minimum is -500,000 + 7k.
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
        assertEquals(8, RotatedMinimum.min(new int[] {31, 46, 52, 8, 14, 22}));
        assertEquals(8, RotatedMinimum.min(new int[] {14, 22, 31, 46, 52, 8}));
    }

    @Test(order = 2)
    void notRotated() {
        assertEquals(-5, RotatedMinimum.min(new int[] {-5, 0, 6, 13}));
        assertEquals(40, RotatedMinimum.min(new int[] {40, 41, 42, 43, 44, 45, 46}));
    }

    @Test(order = 3)
    void singleElement() {
        assertEquals(-4, RotatedMinimum.min(new int[] {-4}));
    }

    @Test(order = 4)
    void twoElements() {
        assertEquals(3, RotatedMinimum.min(new int[] {3, 9}));
        assertEquals(3, RotatedMinimum.min(new int[] {9, 3}));
    }

    @Test(order = 5)
    void threeElementsEveryRotation() {
        assertEquals(1, RotatedMinimum.min(new int[] {1, 3, 5}));
        assertEquals(1, RotatedMinimum.min(new int[] {3, 5, 1}));
        assertEquals(1, RotatedMinimum.min(new int[] {5, 1, 3}));
    }

    @Test(order = 6)
    void everyRotationOfEightValues() {
        int[] sorted = {-20, -11, -3, 0, 4, 15, 27, 39};
        for (int shift = 0; shift < sorted.length; shift++) {
            assertEquals(-20, RotatedMinimum.min(rotate(sorted, shift)), "rotated by " + shift);
        }
    }

    @Test(order = 7)
    void extremeIntValues() {
        assertEquals(Integer.MIN_VALUE, RotatedMinimum.min(new int[] {0, Integer.MAX_VALUE, Integer.MIN_VALUE, -1}));
        assertEquals(Integer.MIN_VALUE, RotatedMinimum.min(new int[] {Integer.MAX_VALUE, Integer.MIN_VALUE}));
    }

    @Test(order = 8, timeoutMs = 400)
    void fortyThousandCallsOnHalfMillionValueArrays() {
        for (int call = 0; call < 40_000; call++) {
            int k = call % SHIFTS.length;
            assertEquals(-500_000 + 7 * k, RotatedMinimum.min(BIG[k]), "array " + k);
        }
    }
}
