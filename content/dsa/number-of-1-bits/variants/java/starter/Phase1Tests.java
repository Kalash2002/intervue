import static kit.Check.assertEquals;

import java.util.Random;
import kit.Test;

public class Phase1Tests {

    /** The 32 bits of a pattern, highest first, in groups of eight. */
    private static String bits(int pattern) {
        String all = String.format("%32s", Integer.toBinaryString(pattern)).replace(' ', '0');
        return all.substring(0, 8) + " " + all.substring(8, 16) + " " + all.substring(16, 24) + " " + all.substring(24);
    }

    private static void expect(int want, int switches) {
        int got = SwitchBoard.countOn(switches);
        if (got != want) { // the message is built only on a failure, since the bulk tests make many calls
            assertEquals(want, got, "countOn(" + switches + "), bits " + bits(switches));
        }
    }

    /** The test's own count: each of the 32 positions in turn. */
    private static int ones(int pattern) {
        int count = 0;
        for (int bit = 0; bit < 32; bit++) {
            count += (pattern >>> bit) & 1;
        }
        return count;
    }

    /** The pattern whose lowest length bits are 1 and the rest 0, for length 0 to 32. */
    private static int lowRun(int length) {
        return (int) ((1L << length) - 1);
    }

    @Test(order = 1, timeoutMs = 1000)
    void sample() {
        expect(6, 1000);
        expect(24, -256);
        expect(1, -2147483648);
    }

    @Test(order = 2, timeoutMs = 1000)
    void zeroAndSmallPatterns() {
        expect(0, 0);
        expect(2, 6);
        expect(3, 7);
        expect(8, 255);
        expect(10, 1023);
        expect(1, 4096);
    }

    @Test(order = 3, timeoutMs = 1000)
    void everySingleBit() {
        for (int bit = 0; bit < 32; bit++) {
            expect(1, 1 << bit); // bit 31 alone is the smallest int
        }
    }

    @Test(order = 4, timeoutMs = 1000)
    void runsOfOnesAtTheBottomAndAtTheTop() {
        for (int length = 0; length <= 32; length++) {
            expect(length, lowRun(length));
            expect(length, ~lowRun(32 - length)); // the top length bits; negative once length > 0
        }
    }

    @Test(order = 5, timeoutMs = 1000)
    void patternsWithTheTopBitSet() {
        expect(2, 0x80000001);
        expect(16, 0xFFFF0000);
        expect(2, 0xC0000000);
        expect(16, 0xAAAAAAAA);
        expect(8, 0xF000000F);
        expect(25, -999);
    }

    @Test(order = 6, timeoutMs = 1000)
    void largePositivePatterns() {
        expect(15, 0x7FFF0000);
        expect(16, 0x55555555);
        expect(28, 0x0FFFFFFF);
        expect(30, 0x3FFFFFFF);
        expect(17, 0x40FFFF00);
    }

    @Test(order = 7, timeoutMs = 1000)
    void everyPatternOfEachHalf() {
        // all 65,536 patterns of the low half alone, of the high half alone, and of the low half
        // under bit 31
        for (int half = 0; half < 1 << 16; half++) {
            expect(ones(half), half);
            expect(ones(half), half << 16);
            expect(ones(half) + 1, 0x80000000 | half);
        }
    }

    @Test(order = 8, timeoutMs = 1000)
    void randomPatterns() {
        Random random = new Random(1613);
        for (int round = 0; round < 100_000; round++) {
            int switches = random.nextInt();
            expect(ones(switches), switches);
        }
    }
}
