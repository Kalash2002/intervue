import static kit.Check.assertEquals;

import java.util.Arrays;
import java.util.Random;
import kit.Test;

public class Phase1Tests {

    private static void expect(int want, int... net) {
        String input = net.length <= 50 ? Arrays.toString(net) : net.length + " days";
        assertEquals(want, Ledger.bestRun(net.clone()), "net = " + input);
    }

    @Test(order = 1)
    void sample() {
        expect(9, 3, -7, 5, -2, 6, -10, 4);
        expect(8, -3, 8, -9, 2, 5);
        expect(-2, -6, -2, -8);
    }

    @Test(order = 2)
    void aLosingDayCanSitInsideTheBestRun() {
        expect(8, 6, -5, 7); // cutting the ledger at the -5 leaves 7 at best
        expect(11, 4, -1, 2, -3, 9); // the whole ledger; cutting at the losses leaves 9
    }

    @Test(order = 3)
    void everyDayLost() {
        expect(-1, -7, -1, -3, -1);
        expect(-4, -4, -4, -4);
        expect(-10_000, -10_000, -10_000);
    }

    @Test(order = 4)
    void oneDay() {
        expect(5, 5);
        expect(-9, -9);
        expect(0, 0);
    }

    @Test(order = 5)
    void zerosBetweenLosses() {
        expect(0, 0, -3, 0);
        expect(0, -2, 0, -1);
    }

    @Test(order = 6)
    void theBestRunAtEitherEnd() {
        expect(8, -4, 1, 7); // ends on the last day
        expect(3, 2, -5, 3); // the last day on its own
        expect(8, 7, 1, -9, 3); // starts on the first day
    }

    @Test(order = 7)
    void randomShortLedgers() {
        Random random = new Random(6341);
        for (int round = 0; round < 300; round++) {
            int[] net = new int[1 + random.nextInt(12)];
            for (int i = 0; i < net.length; i++) {
                net[i] = random.nextInt(19) - 9;
            }
            expect(everyRun(net), net);
        }
    }

    /** Adds up every run from scratch. */
    private static int everyRun(int[] net) {
        int best = Integer.MIN_VALUE;
        for (int start = 0; start < net.length; start++) {
            for (int end = start; end < net.length; end++) {
                int total = 0;
                for (int i = start; i <= end; i++) {
                    total += net[i];
                }
                best = Math.max(best, total);
            }
        }
        return best;
    }

    @Test(order = 8, timeoutMs = 200)
    void aLongLedgerOfGains() {
        int[] net = new int[100_000];
        Arrays.fill(net, 10_000);
        expect(1_000_000_000, net); // every day, the largest total the constraints allow
    }

    @Test(order = 9, timeoutMs = 200)
    void aLongLedgerWithLossesBetweenGains() {
        // Two gains of 10,000 and a loss of 9,999, repeated, so every three days add 10,001. Day 0
        // is a loss of 10,000 instead and is left out: days 1 and 2 add 1, then 33,332 full rounds
        // of three days, then a last gain of 10,000.
        int[] net = new int[100_000];
        for (int i = 0; i < net.length; i++) {
            net[i] = i % 3 == 2 ? -9_999 : 10_000;
        }
        net[0] = -10_000;
        expect(333_363_333, net); // 33,333 x 10,001
    }
}
