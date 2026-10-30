import static kit.Check.assertEquals;
import static kit.Check.assertTrue;

import kit.Test;

public class Phase1Tests {

    /** Checks build(n) against want by length and entry by entry. */
    private static void expect(int[] want, int n) {
        int[] got = BitTable.build(n);
        assertTrue(got != null, "build(" + n + ") returned null");
        assertEquals(n + 1, got.length, "build(" + n + ") must have n + 1 entries, for 0 to " + n);
        for (int i = 0; i <= n; i++) {
            if (got[i] != want[i]) {
                assertEquals(want[i], got[i], "build(" + n + ") entry " + i + ", which is " + Integer.toBinaryString(i) + " in binary");
            }
        }
    }

    /** The test's own table: each number's bits counted one at a time. */
    private static int[] counted(int n) {
        int[] ones = new int[n + 1];
        for (int i = 0; i <= n; i++) {
            for (int rest = i; rest != 0; rest >>= 1) {
                ones[i] += rest & 1;
            }
        }
        return ones;
    }

    @Test(order = 1)
    void sample() {
        expect(new int[] {0, 1, 1, 2, 1, 2, 2, 3, 1, 2, 2}, 10);
        expect(new int[] {0, 1, 1, 2, 1, 2, 2, 3, 1, 2, 2, 3, 2, 3, 3, 4, 1}, 16);
        expect(new int[] {0}, 0);
    }

    @Test(order = 2)
    void aTableThatEndsOnSeveralOnes() {
        expect(new int[] {0, 1, 1, 2, 1, 2, 2, 3, 1, 2, 2, 3, 2, 3, 3}, 14);
        expect(counted(31), 31);
    }

    @Test(order = 3)
    void everyTableUpTo300() {
        for (int n = 0; n <= 300; n++) {
            expect(counted(n), n);
        }
    }

    @Test(order = 4)
    void powersOfTwoAndOneBelow() {
        for (int n : new int[] {64, 63, 1023, 1024, 4095, 4096, 65535, 65536}) {
            expect(counted(n), n);
        }
        int[] table = counted(65536);
        assertEquals(16, table[65535], "the test's own count of 65535");
        assertEquals(1, table[65536], "the test's own count of 65536");
    }

    @Test(order = 5)
    void theLargestTable() {
        int[] want = counted(100_000);
        // spot checks of the test's own table: 98303 is 10111111111111111, the most 1 bits up to 10^5
        assertEquals(16, want[98_303], "the test's own count of 98303");
        assertEquals(10, want[99_999], "the test's own count of 99999");
        assertEquals(6, want[100_000], "the test's own count of 100000");
        expect(want, 100_000);
    }

    @Test(order = 6)
    void otherLargeSizes() {
        for (int n : new int[] {99_999, 77_777, 31_416, 13_469}) {
            expect(counted(n), n);
        }
    }
}
