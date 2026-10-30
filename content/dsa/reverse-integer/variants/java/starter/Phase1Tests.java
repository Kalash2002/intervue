import static kit.Check.assertEquals;

import java.util.Random;
import kit.Test;

public class Phase1Tests {

    private static void expect(int want, int x) {
        int got = DigitMirror.reverse(x);
        if (got != want) { // the message is built only on a failure, since the bulk tests make many calls
            assertEquals(want, got, "reverse(" + x + ")");
        }
    }

    /** The digits of a non-negative number in reverse order, as a long. */
    private static long reversedDigits(long value) {
        return Long.parseLong(new StringBuilder(Long.toString(value)).reverse().toString());
    }

    /** The test's own answer: the digits reversed as text, with 0 when the result is no int. */
    private static int byText(int x) {
        long value = reversedDigits(Math.abs((long) x));
        long signed = x < 0 ? -value : value;
        return signed < Integer.MIN_VALUE || signed > Integer.MAX_VALUE ? 0 : (int) signed;
    }

    @Test(order = 1)
    void sample() {
        expect(10385, 58301);
        expect(-49, -9400);
        expect(0, 1563847412);
    }

    @Test(order = 2)
    void zeroAndSingleDigits() {
        expect(0, 0);
        expect(7, 7);
        expect(-4, -4);
        expect(9, 9);
        expect(-9, -9);
    }

    @Test(order = 3)
    void trailingZerosAreDropped() {
        expect(54, 4500);
        expect(-307, -70300);
        expect(8, 8000000);
        expect(741, 1470000000);
        expect(-4, -400);
        expect(-2, -2000000000);
    }

    @Test(order = 4)
    void negativeInputs() {
        expect(-85, -58);
        expect(-606, -6060);
        expect(-468, -864);
        expect(-1463847412, -2147483641);
        expect(0, -1000000003); // -3,000,000,001 is below Integer.MIN_VALUE
    }

    @Test(order = 5)
    void resultsThatJustFitAndJustDoNot() {
        expect(2147483641, 1463847412); // the largest result there is: 6 below Integer.MAX_VALUE
        expect(0, 1473847412); // 2,147,483,741 is 94 too large
        expect(-2147483641, -1463847412);
        expect(0, -1563847412); // -2,147,483,651 is 3 below Integer.MIN_VALUE
        expect(2000000001, 1000000002);
        expect(0, 1000000003);
        expect(-2000000001, -1000000002);
    }

    @Test(order = 6)
    void theIntLimits() {
        expect(0, Integer.MAX_VALUE); // 7,463,847,412
        expect(0, Integer.MIN_VALUE); // its absolute value is no int either
        expect(0, Integer.MIN_VALUE + 1);
        expect(1463847412, 2147483641);
    }

    @Test(order = 7)
    void numbersThatReadTheSameBothWays() {
        expect(474, 474);
        expect(-35753, -35753);
        expect(1987887891, 1987887891);
        expect(-2000000002, -2000000002);
    }

    @Test(order = 8)
    void everyNumberUpToAHundredThousand() {
        for (int x = -100_000; x <= 100_000; x++) {
            expect(byText(x), x);
        }
    }

    @Test(order = 9)
    void resultsAroundTheLimits() {
        // every result from 2,147,480,001 to 2,147,489,999 whose reverse is an int, and its
        // negative: 422 of them fit, and 725 are too large by 4 to 6,344
        for (long result = 2_147_480_001L; result <= 2_147_489_999L; result++) {
            long x = reversedDigits(result);
            if (result % 10 == 0 || x > Integer.MAX_VALUE) {
                continue;
            }
            int want = result <= Integer.MAX_VALUE ? (int) result : 0;
            expect(want, (int) x);
            expect(-want, (int) -x);
        }
    }

    @Test(order = 10)
    void randomNumbers() {
        Random random = new Random(1847);
        for (int round = 0; round < 200_000; round++) {
            int x = random.nextInt();
            expect(byText(x), x);
            int nineDigits = random.nextInt(1_000_000_000); // all of these fit once reversed (nine digits at most)
            expect(byText(nineDigits), nineDigits);
        }
    }
}
