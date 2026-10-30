import static kit.Check.fail;

import java.util.Random;
import kit.Test;

public class Phase1Tests {

    private static final int MIN = Integer.MIN_VALUE;
    private static final int MAX = Integer.MAX_VALUE;

    /** Accepts an answer within 10^-6 relative error of want, with an absolute floor of 10^-12; NaN and infinities fail. */
    private static void expect(double want, double x, int n) {
        double got = Power.raise(x, n);
        double tolerance = 1e-6 * Math.abs(want) + 1e-12;
        if (!(Math.abs(got - want) <= tolerance)) { // written this way round so that NaN fails too
            fail("raise(" + x + ", " + n + "): expected " + want + " (within " + tolerance + ") but was " + got);
        }
    }

    /** The JDK's power, within one unit in the last place of the exact value: the answer for inputs too large to write out. */
    private static void expectLikeTheJdk(double x, int n) {
        expect(Math.pow(x, n), x, n);
    }

    @Test(order = 1, timeoutMs = 300)
    void sample() {
        expect(38.443359375, 1.5, 9);
        expect(-2048.0, -0.5, -11);
        expect(1.0, -1.0, MIN);
    }

    @Test(order = 2)
    void theZeroPowerIsOne() {
        expect(1.0, -37.25, 0);
        expect(1.0, 99.99, 0);
        expect(1.0, 0.001, 0);
        expect(1.0, -1.0, 0);
    }

    @Test(order = 3)
    void smallPositivePowers() {
        // every value here is a fraction over a power of 2, so a double holds it exactly
        expect(1.5, 1.5, 1);
        expect(-2187.0, -3.0, 7);
        expect(0.000244140625, 0.25, 6);
        expect(9900.25, 99.5, 2);
        expect(-157.2763671875, -2.75, 5);
    }

    @Test(order = 4)
    void negativePowersAreReciprocals() {
        expect(8192.0, 0.5, -13);
        expect(-0.0009765625, -4.0, -5);
        expect(4096.0, -0.125, -4);
        expect(0.000244140625, 8.0, -4);
        expect(0.0256, 2.5, -4); // 16/625, the double nearest it
        expect(0.8, 1.25, -1);
    }

    @Test(order = 5)
    void negativeBases() {
        expect(-17.0859375, -1.5, 7);
        expect(25.62890625, -1.5, 8);
        expect(-0.2373046875, -0.75, 5);
        expect(9801.0, -99.0, 2);
        expect(-1.0 / 27, -3.0, -3);
    }

    @Test(order = 6)
    void zeroToAPositivePowerIsZero() {
        expect(0.0, 0.0, 1);
        expect(0.0, 0.0, 6);
        expect(0.0, 0.0, 1000);
    }

    @Test(order = 7, timeoutMs = 300)
    void theSmallestIntExponent() {
        // -n does not fit in an int here; the results stay in range because x is 1, 4 or within 10^-9 of 1 in size
        expect(1.0, -1.0, MIN);
        expect(1.0, 1.0, MIN);
        expect(0.0, 4.0, MIN);
        expect(0.0, -4.0, MIN);
        expectLikeTheJdk(1.0000000004, MIN);
        expectLikeTheJdk(-0.9999999995, MIN); // positive: the exponent is even
    }

    @Test(order = 8, timeoutMs = 300)
    void exponentsNearTheIntLimits() {
        expectLikeTheJdk(1.000000001, MAX);
        expectLikeTheJdk(0.9999999987, 2_000_000_000);
        expectLikeTheJdk(1.0000000031, -1_500_000_000);
        expectLikeTheJdk(-0.9999999993, 2_147_483_645); // negative: the exponent is odd
        expect(0.0, 0.0, MAX);
        expect(1.0, 1.0, MAX);
        expect(-1.0, -1.0, MAX);
        expect(0.0, 0.5, MAX);
    }

    @Test(order = 9)
    void randomPowers() {
        Random random = new Random(1411);
        int checked = 0;
        while (checked < 300) {
            int n = random.nextInt(6001) - 3000;
            if (n == 0) {
                continue;
            }
            // pick the size of the answer, e^u with u from -9 to 9, then the base that gives it
            double u = random.nextDouble() * 18 - 9;
            double x = Math.exp(u / n) * (random.nextBoolean() ? 1 : -1);
            double want = Math.pow(x, n);
            if (Math.abs(x) >= 100 || Math.abs(want) > 1e4) {
                continue; // outside the constraints
            }
            expect(want, x, n);
            checked++;
        }
    }
}
