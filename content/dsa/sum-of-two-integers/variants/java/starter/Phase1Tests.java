import static kit.Check.assertEquals;

import java.util.Random;
import kit.Test;

public class Phase1Tests {

    private static final int MAX = Integer.MAX_VALUE;
    private static final int MIN = Integer.MIN_VALUE;

    /** The 32 bits of a word, highest first, in groups of eight. */
    private static String bits(int word) {
        String all = String.format("%32s", Integer.toBinaryString(word)).replace(' ', '0');
        return all.substring(0, 8) + " " + all.substring(8, 16) + " " + all.substring(16, 24) + " " + all.substring(24);
    }

    private static void expect(int want, int a, int b) {
        int got = LogicAdder.add(a, b);
        if (got != want) { // the message is built only on a failure, since the bulk tests make many calls
            String call = "add(" + a + ", " + b + ") should be " + want + " but was " + got;
            assertEquals(bits(want), bits(got), call);
        }
    }

    /** Checks add(a, b) against Java's own int addition, which the tests are free to use. */
    private static void expectJavaSum(int a, int b) {
        expect(a + b, a, b);
    }

    /** The word whose lowest length bits are 1 and the rest 0, for length 0 to 32. */
    private static int lowRun(int length) {
        return (int) ((1L << length) - 1);
    }

    @Test(order = 1, timeoutMs = 1000)
    void sample() {
        expect(64, 19, 45);
        expect(-13, -38, 25);
        expect(-2147483296, 2147483000, 1000);
    }

    @Test(order = 2, timeoutMs = 1000)
    void zeroOperands() {
        expect(0, 0, 0);
        expect(-613, 0, -613);
        expect(8080, 8080, 0);
        expect(MIN, 0, MIN);
        expect(MAX, MAX, 0);
    }

    @Test(order = 3, timeoutMs = 1000)
    void negativeOperands() {
        expect(-264, -250, -14);
        expect(-154, -77, -77);
        expect(-128, -64, -64);
        expect(-2097152, -1048576, -1048576);
        expect(-154320, -99999, -54321);
    }

    @Test(order = 4, timeoutMs = 1000)
    void mixedSigns() {
        expect(0, -500, 500); // the carry runs out of bit 31 and is dropped
        expect(-1, 731, -732);
        expect(910, -90, 1000);
        expect(-1, 64, -65);
        expect(256, -4096, 4352);
        expect(-1, MIN, MAX);
    }

    @Test(order = 5, timeoutMs = 1000)
    void sumsThatWrapAround() {
        expect(-2147483639, MAX, 10);
        expect(-2, MAX, MAX);
        expect(-1294967296, 1500000000, 1500000000);
        expect(0, MIN, MIN);
        expect(2147483638, MIN, -10);
        expect(1294967296, -1500000000, -1500000000);
        expect(1394967296, -2000000000, -900000000);
    }

    @Test(order = 6, timeoutMs = 1000)
    void longCarries() {
        expect(0, -16, 16); // a carry from bit 4 through bit 31
        expect(0x00010000, 0x0000FFF0, 0x10);
        expect(MIN, 0x7FFFFFF0, 0x10); // the carry ends in bit 31, the sign bit
        expect(0x1FFFFFFE, 0x0FFFFFFF, 0x0FFFFFFF);
        expect(0xAAAAAAAA, 0x55555555, 0x55555555);
        expect(0xFFFFFFFF, 0xAAAAAAAA, 0x55555555); // no column makes a carry
    }

    // the two bulk tests, this one and randomPairs, make about 510,000 calls, so a correct
    // solution through strings needs more than a second on a busy machine; the other tests
    // stop an endless loop in 1 s
    @Test(order = 7, timeoutMs = 5000)
    void everyPairOfSmallValues() {
        for (int a = -256; a <= 256; a++) {
            for (int b = -256; b <= 256; b++) {
                expectJavaSum(a, b);
            }
        }
    }

    @Test(order = 8, timeoutMs = 1000)
    void runsOfOnesAtTheBottomAndAtTheTop() {
        // every single bit, and runs of ones from either end, so carries of every length
        for (int i = 0; i <= 32; i++) {
            for (int j = 0; j <= 32; j++) {
                expectJavaSum(lowRun(i), lowRun(j));
                expectJavaSum(~lowRun(i), lowRun(j));
                expectJavaSum(~lowRun(i), ~lowRun(j));
                if (i < 32 && j < 32) {
                    expectJavaSum(1 << i, 1 << j);
                }
            }
        }
    }

    @Test(order = 9, timeoutMs = 5000) // a bulk test: see everyPairOfSmallValues
    void randomPairs() {
        Random random = new Random(1831);
        for (int round = 0; round < 200_000; round++) {
            expectJavaSum(random.nextInt(), random.nextInt());
        }
        for (int round = 0; round < 25_000; round++) { // near the limits, where sums wrap
            expectJavaSum(MAX - random.nextInt(1 << 20), MAX - random.nextInt(1 << 20));
            expectJavaSum(MIN + random.nextInt(1 << 20), MIN + random.nextInt(1 << 20));
        }
    }
}
