import static kit.Check.assertEquals;

import java.util.Random;
import kit.Test;

public class Phase1Tests {

    /** The 32 bits of a word, highest first, in groups of eight. */
    private static String bits(int word) {
        String all = String.format("%32s", Integer.toBinaryString(word)).replace(' ', '0');
        return all.substring(0, 8) + " " + all.substring(8, 16) + " " + all.substring(16, 24) + " " + all.substring(24);
    }

    private static void expect(int want, int word) {
        int got = WireOrder.mirror(word);
        if (got != want) { // the message is built only on a failure, since the bulk tests make many calls
            String call = "mirror(" + word + "), bits " + bits(word) + ", should be " + want + " but was " + got;
            assertEquals(bits(want), bits(got), call);
        }
    }

    /** The test's own mirror: each bit i of word put at position 31 - i. */
    private static int mirrored(int word) {
        int out = 0;
        for (int i = 0; i < 32; i++) {
            out |= ((word >>> i) & 1) << (31 - i);
        }
        return out;
    }

    /** The word whose lowest length bits are 1 and the rest 0, for length 0 to 32. */
    private static int lowRun(int length) {
        return (int) ((1L << length) - 1);
    }

    @Test(order = 1, timeoutMs = 1000)
    void sample() {
        expect(-1543503872, 37);
        expect(268435455, -16);
        expect(-2147483648, 1);
    }

    @Test(order = 2, timeoutMs = 1000)
    void zeroAndTheMiddleBits() {
        expect(0, 0);
        expect(0x00008000, 0x00010000); // bit 16 goes to bit 15
        expect(0x00010000, 0x00008000);
        expect(0x00018000, 0x00018000); // bits 15 and 16 swap with each other
    }

    @Test(order = 3, timeoutMs = 1000)
    void everySingleBit() {
        for (int bit = 0; bit < 32; bit++) {
            expect(1 << (31 - bit), 1 << bit);
        }
    }

    @Test(order = 4, timeoutMs = 1000)
    void theZerosAboveTheHighestOneTakePart() {
        expect(0x60000000, 6);
        expect(0xFF000000, 0xFF);
        expect(0x17C00000, 1000);
        expect(0xFFFF0000, 0x0000FFFF);
        expect(0xF00F0000, 0x0000F00F);
        expect(0x1E6A2C48, 0x12345678);
    }

    @Test(order = 5, timeoutMs = 1000)
    void wordsWithTheTopBitSet() {
        expect(1, 0x80000000);
        expect(0x0000000F, 0xF0000000);
        expect(0x00000007, 0xE0000000);
        expect(0x55555555, 0xAAAAAAAA);
        expect(0x00000F01, 0x80F00000);
        expect(0x983FFFFF, -999);
        expect(0x000000FE, 0x7F000000); // bit 31 is 0 here, and bit 0 of the result too
    }

    @Test(order = 6, timeoutMs = 1000)
    void wordsThatAreTheirOwnMirror() {
        expect(0x80000001, 0x80000001);
        expect(0x81818181, 0x81818181);
        expect(0x0FF00FF0, 0x0FF00FF0);
        expect(0xC3C3C3C3, 0xC3C3C3C3);
    }

    @Test(order = 7, timeoutMs = 1000)
    void runsOfOnesAtTheBottomAndAtTheTop() {
        for (int length = 0; length <= 32; length++) {
            expect(~lowRun(32 - length), lowRun(length)); // the lowest length bits become the top ones
            expect(lowRun(length), ~lowRun(32 - length));
        }
    }

    // the two bulk tests make about 330,000 calls, so a correct solution through strings needs
    // more than a second on a busy machine; the small tests above stop an endless loop in 1 s
    @Test(order = 8, timeoutMs = 5000)
    void everyPatternOfEachHalf() {
        for (int half = 0; half < 1 << 16; half++) {
            expect(mirrored(half), half);
            expect(mirrored(half << 16), half << 16); // negative once half reaches 1 << 15
        }
    }

    @Test(order = 9, timeoutMs = 5000)
    void randomWords() {
        Random random = new Random(1619);
        for (int round = 0; round < 100_000; round++) {
            int word = random.nextInt();
            int want = mirrored(word);
            expect(want, word);
            expect(word, want); // mirroring twice gives the word back
        }
    }
}
