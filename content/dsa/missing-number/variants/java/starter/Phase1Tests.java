import static kit.Check.assertEquals;

import java.util.Arrays;
import java.util.Random;
import kit.Test;

public class Phase1Tests {

    private static void expect(int want, int[] nums) {
        // the call is written down first, in case the solution reorders nums
        String call = nums.length <= 20 ? "find(" + Arrays.toString(nums) + ")" : "find(an array of " + nums.length + " values)";
        assertEquals(want, LostTicket.find(nums), call);
    }

    /** The numbers 0 to n except missing, in increasing order. */
    private static int[] without(int n, int missing) {
        int[] nums = new int[n];
        int at = 0;
        for (int number = 0; number <= n; number++) {
            if (number != missing) {
                nums[at++] = number;
            }
        }
        return nums;
    }

    private static int[] shuffled(int[] a, Random random) {
        for (int j = a.length - 1; j > 0; j--) {
            swap(a, j, random.nextInt(j + 1));
        }
        return a;
    }

    private static int[] reversed(int[] a) {
        reverseFrom(a, 0);
        return a;
    }

    private static void reverseFrom(int[] a, int from) {
        for (int i = from, j = a.length - 1; i < j; i++, j--) {
            swap(a, i, j);
        }
    }

    private static void swap(int[] a, int i, int j) {
        int t = a[i];
        a[i] = a[j];
        a[j] = t;
    }

    /** Rearranges a into the next permutation in increasing order; false after the last one. */
    private static boolean nextPermutation(int[] a) {
        int i = a.length - 2;
        while (i >= 0 && a[i] > a[i + 1]) {
            i--;
        }
        if (i < 0) {
            return false;
        }
        int j = a.length - 1;
        while (a[j] < a[i]) {
            j--;
        }
        swap(a, i, j);
        reverseFrom(a, i + 1);
        return true;
    }

    @Test(order = 1)
    void sample() {
        expect(5, new int[] {4, 7, 1, 0, 6, 2, 3});
        expect(0, new int[] {3, 5, 1, 4, 2});
        expect(1, new int[] {0});
    }

    @Test(order = 2)
    void theMissingNumberIsZeroOrN() {
        expect(0, new int[] {1});
        expect(0, new int[] {8, 3, 6, 1, 10, 4, 7, 2, 9, 5});
        expect(0, new int[] {12, 4, 10, 7, 1, 11, 3, 9, 6, 2, 8, 5});
        expect(10, new int[] {5, 9, 0, 7, 2, 8, 4, 1, 6, 3}); // n itself: no index is 10
        expect(11, new int[] {7, 3, 10, 0, 5, 9, 1, 6, 2, 8, 4});
    }

    @Test(order = 3)
    void theMissingNumberInTheMiddle() {
        expect(5, new int[] {10, 2, 7, 0, 9, 4, 1, 8, 3, 6});
        expect(3, new int[] {6, 0, 5, 1, 4, 2});
        expect(8, new int[] {13, 6, 0, 11, 2, 9, 4, 12, 1, 7, 10, 3, 5});
    }

    @Test(order = 4)
    void sortedAndReversedOrder() {
        expect(13, without(20, 13));
        expect(1, without(50, 1));
        expect(17, reversed(without(31, 17)));
        expect(0, reversed(without(26, 0)));
    }

    @Test(order = 5)
    void everySmallCaseInEveryOrder() {
        for (int n = 1; n <= 7; n++) {
            for (int missing = 0; missing <= n; missing++) {
                int[] order = without(n, missing);
                do {
                    expect(missing, order.clone());
                } while (nextPermutation(order));
            }
        }
    }

    @Test(order = 6)
    void randomArrays() {
        Random random = new Random(1801);
        for (int round = 0; round < 300; round++) {
            int n = 1 + random.nextInt(2000);
            int missing = random.nextInt(n + 1);
            expect(missing, shuffled(without(n, missing), random));
        }
    }

    @Test(order = 7)
    void sumsPastTheIntRange() {
        // from n = 46,341 on, n * (n + 1) no longer fits in an int, and up to n = 80,264 the
        // int product halved is off by 2^31; from n = 65,536 on, the sum of 0 to n passes it too
        Random random = new Random(1811);
        expect(23170, shuffled(without(46341, 23170), random));
        expect(0, shuffled(without(65536, 0), random));
        expect(4021, shuffled(without(80264, 4021), random));
        expect(77, shuffled(without(100000, 77), random)); // the largest n
    }

    @Test(order = 8, timeoutMs = 400)
    void largeArraysWithTheMissingNumberInTheMiddle() {
        Random random = new Random(1823);
        int n = 100_000;
        for (int round = 0; round < 8; round++) {
            // fully shuffled, and the missing number in the middle third of the range, so that
            // trying the numbers from 0 up or from n down each scans about half the array
            // for each of a third to two thirds of the numbers
            int missing = n / 3 + random.nextInt(n / 3);
            expect(missing, shuffled(without(n, missing), random));
        }
    }
}
