import static kit.Check.assertEquals;

import kit.Test;

public class Phase1Tests {

    // Built once per test JVM: five pairs of large sorted arrays whose medians follow from how
    // they are built (worked out next to each one).
    private static final int[][] A = new int[5][];
    private static final int[][] B = new int[5][];
    private static final double[] MEDIAN = new double[5];

    static {
        int million = 1_000_000;
        // 0: evens and odds below 2,000,000 merge into 0 .. 1,999,999; median of 999,999 and 1,000,000
        A[0] = new int[million];
        B[0] = new int[million];
        for (int i = 0; i < million; i++) {
            A[0][i] = 2 * i;
            B[0][i] = 2 * i + 1;
        }
        MEDIAN[0] = 999_999.5;
        // 1: a is empty; b = 3i - 1,000,000 for 999,999 values, median at i = 499,999
        A[1] = new int[0];
        B[1] = new int[999_999];
        for (int i = 0; i < B[1].length; i++) {
            B[1][i] = 3 * i - million;
        }
        MEDIAN[1] = 499_997;
        // 2: a = {MAX}; b = 0 .. 999,999; 1,000,001 values, the middle one is b[500,000]
        A[2] = new int[] {Integer.MAX_VALUE};
        B[2] = new int[million];
        for (int i = 0; i < million; i++) {
            B[2][i] = i;
        }
        MEDIAN[2] = 500_000;
        // 3: every value of a (1,000,000 .. 1,499,999) is above every value of b (0 .. 999,999);
        // together 0 .. 1,499,999, median of 749,999 and 750,000
        A[3] = new int[500_000];
        for (int i = 0; i < A[3].length; i++) {
            A[3][i] = million + i;
        }
        B[3] = B[2];
        MEDIAN[3] = 749_999.5;
        // 4: a = 400,000 sevens; b = -300,000 .. 699,999. Values below 7: 300,007 from b; the sevens
        // (400,001 of them with b's own 7) cover sorted positions 300,007 .. 700,007, which hold
        // the middle positions 699,999 and 700,000 of the 1,400,000 values
        A[4] = new int[400_000];
        java.util.Arrays.fill(A[4], 7);
        B[4] = new int[million];
        for (int i = 0; i < million; i++) {
            B[4][i] = i - 300_000;
        }
        MEDIAN[4] = 7.0;
    }

    @Test(order = 1)
    void sample() {
        assertEquals(10.0, SortedMedian.median(new int[] {3, 8, 21}, new int[] {5, 10, 13, 30}));
        assertEquals(3.5, SortedMedian.median(new int[] {-4, 6}, new int[] {1, 9}));
    }

    @Test(order = 2)
    void oneArrayEmpty() {
        assertEquals(7.0, SortedMedian.median(new int[] {}, new int[] {7}));
        assertEquals(1.0, SortedMedian.median(new int[] {-2, 4}, new int[] {}));
        assertEquals(2.0, SortedMedian.median(new int[] {1, 2, 9}, new int[] {}));
    }

    @Test(order = 3)
    void averageOfTwoHalvesIsNotTruncated() {
        assertEquals(1.5, SortedMedian.median(new int[] {1}, new int[] {2}));
        assertEquals(3.5, SortedMedian.median(new int[] {6}, new int[] {1, 2, 3, 4, 5}));
    }

    @Test(order = 4)
    void extremeValuesDoNotOverflow() {
        int max = Integer.MAX_VALUE;
        int min = Integer.MIN_VALUE;
        assertEquals(2147483647.0, SortedMedian.median(new int[] {max}, new int[] {max}));
        assertEquals(-2147483648.0, SortedMedian.median(new int[] {min}, new int[] {min}));
        assertEquals(-0.5, SortedMedian.median(new int[] {min}, new int[] {max}));
        assertEquals(2147483646.5, SortedMedian.median(new int[] {max - 1, max}, new int[] {min, max}));
    }

    @Test(order = 5)
    void oneArrayEntirelyBeforeTheOther() {
        assertEquals(10.0, SortedMedian.median(new int[] {1, 2, 3}, new int[] {10, 11, 12, 13}));
        assertEquals(10.0, SortedMedian.median(new int[] {10, 11, 12, 13}, new int[] {1, 2, 3}));
        assertEquals(6.5, SortedMedian.median(new int[] {1, 2, 3, 4, 5, 6}, new int[] {7, 8, 9, 10, 11, 12}));
    }

    @Test(order = 6)
    void repeatedValues() {
        assertEquals(4.0, SortedMedian.median(new int[] {4, 4, 4}, new int[] {4, 4}));
        assertEquals(5.0, SortedMedian.median(new int[] {1, 1, 9}, new int[] {1, 9, 9}));
    }

    @Test(order = 7)
    void interleavedAndNegative() {
        assertEquals(-1.0, SortedMedian.median(new int[] {-9, -3, 0}, new int[] {-7, -1, 2, 8}));
        assertEquals(5.0, SortedMedian.median(new int[] {2, 5}, new int[] {1, 3, 4, 6, 7, 8, 9}));
    }

    @Test(order = 8, timeoutMs = 400)
    void twoThousandMediansOfMillionValueArrays() {
        for (int call = 0; call < 2_000; call++) {
            int p = call % A.length;
            assertEquals(MEDIAN[p], SortedMedian.median(A[p], B[p]), "pair " + p);
        }
    }
}
