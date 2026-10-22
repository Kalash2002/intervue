import static kit.Check.assertEquals;

import java.util.Arrays;
import java.util.Random;
import kit.Test;

public class Phase1Tests {

    private static void expect(long want, int... gains) {
        String input = gains.length <= 50 ? Arrays.toString(gains) : gains.length + " stages";
        assertEquals(want, Amplifier.bestGain(gains.clone()), "gains = " + input);
    }

    @Test(order = 1)
    void sample() {
        expect(48, 7, -1, 2, -3, -2, 4);
        expect(60, 3, -5, 2, -2);
        expect(5, -6, 5, 0, 2, -1, 3);
        expect(-7, -7);
    }

    @Test(order = 2)
    void twoNegativesCancelLater() {
        expect(90, -3, 5, -6); // the largest product ending at 5 is 5, but -15 times -6 is 90
        expect(16, 4, -1, -1, 4);
    }

    @Test(order = 3)
    void anOddNumberOfNegativesDropsOneEnd() {
        expect(144, 6, -1, 3, -2, 4, -1); // everything but the last stage
        expect(60, -2, 5, -1, 3, -4); // everything but the first stage
    }

    @Test(order = 4)
    void zerosSplitTheChain() {
        expect(0, -5, 0, -3, 0, -7); // every run without a 0 is negative
        expect(0, 0);
        expect(40, 0, -4, 5, -2, 0);
        expect(9, 8, 0, 0, -1, -9);
    }

    @Test(order = 5)
    void aSingleStage() {
        expect(-4, -4);
        expect(10, 10);
    }

    @Test(order = 6)
    void productsPastTheIntRange() {
        expect(10_000_000_000L, -10, 10, 10, 10, 10, 10, 10, 10, 10, -10);
        int[] threes = new int[39];
        Arrays.fill(threes, 3);
        expect(4_052_555_153_018_976_267L, threes); // 3^39, odd, so a double cannot hold it exactly
        int[] twos = new int[62];
        Arrays.fill(twos, 2);
        twos[0] = -2;
        twos[61] = -2;
        expect(4_611_686_018_427_387_904L, twos); // 2^62
    }

    @Test(order = 7)
    void randomShortChains() {
        Random random = new Random(1907);
        for (int round = 0; round < 300; round++) {
            int[] gains = new int[1 + random.nextInt(12)];
            for (int i = 0; i < gains.length; i++) {
                gains[i] = random.nextInt(9) - 4;
            }
            expect(everyRun(gains), gains);
        }
    }

    /** Multiplies out every run from scratch. */
    private static long everyRun(int[] gains) {
        long best = Long.MIN_VALUE;
        for (int start = 0; start < gains.length; start++) {
            for (int end = start; end < gains.length; end++) {
                long product = 1;
                for (int i = start; i <= end; i++) {
                    product *= gains[i];
                }
                best = Math.max(best, product);
            }
        }
        return best;
    }

    @Test(order = 8, timeoutMs = 300)
    void aLongChainWithoutZeros() {
        // 101 gains of -1 (stage 0 and every stage 7 mod 1000) and 61 gains of 2. The whole chain
        // is negative; leaving out stage 0, or everything from the last -1 on, keeps all 61 twos
        // with an even number of -1s.
        int[] gains = new int[100_000];
        Arrays.fill(gains, 1);
        gains[0] = -1;
        for (int i = 7; i < gains.length; i += 1000) {
            gains[i] = -1;
        }
        for (int k = 0; k <= 60; k++) {
            gains[1000 + 1500 * k] = 2;
        }
        expect(2_305_843_009_213_693_952L, gains); // 2^61
    }

    @Test(order = 9, timeoutMs = 300)
    void aLongChainSplitByAZero() {
        // Left of the 0: a -2 at stage 0, then 40 twos, so the best there is 2^40. Right of it:
        // -1 at stages 60000, 70000 and 80000, with 20 threes before the first, 10 between the
        // first and second and 5 after the last. Stopping before the last -1 keeps 30 threes.
        int[] gains = new int[100_000];
        Arrays.fill(gains, 1);
        gains[0] = -2;
        for (int k = 1; k <= 40; k++) {
            gains[1000 * k] = 2;
        }
        gains[50_000] = 0;
        gains[60_000] = -1;
        gains[70_000] = -1;
        gains[80_000] = -1;
        for (int j = 0; j < 20; j++) {
            gains[50_100 + 300 * j] = 3;
        }
        for (int j = 0; j < 10; j++) {
            gains[61_000 + 500 * j] = 3;
        }
        for (int j = 0; j < 5; j++) {
            gains[85_000 + 1000 * j] = 3;
        }
        expect(205_891_132_094_649L, gains); // 3^30
    }
}
