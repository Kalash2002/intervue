import static kit.Check.assertEquals;
import static kit.Check.assertTrue;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Random;
import kit.Test;

public class Phase1Tests {

    private static String show(int[] digits) {
        if (digits.length <= 24) {
            return Arrays.toString(digits);
        }
        StringBuilder start = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            start.append(digits[i]);
        }
        return "a " + digits.length + "-digit number starting " + start;
    }

    /** Checks plusOne(digits) against want; digits may be changed by the call, so it is shown first. */
    private static void expect(int[] want, int[] digits) {
        String call = "plusOne(" + show(digits) + ")";
        int[] got = Odometer.plusOne(digits);
        assertTrue(got != null, call + " returned null");
        if (!Arrays.equals(want, got)) {
            assertEquals(show(want), show(got), call);
            assertEquals(Arrays.toString(want), Arrays.toString(got), call); // same start, so show every digit
        }
    }

    private static int[] repeat(int digit, int length) {
        int[] digits = new int[length];
        Arrays.fill(digits, digit);
        return digits;
    }

    /** The digits of a decimal string. */
    private static int[] digitsOf(String number) {
        int[] digits = new int[number.length()];
        for (int i = 0; i < digits.length; i++) {
            digits[i] = number.charAt(i) - '0';
        }
        return digits;
    }

    /** The test's own answer, through BigInteger, which a solution has no need for. */
    private static int[] plusOneByValue(int[] digits) {
        StringBuilder number = new StringBuilder();
        for (int digit : digits) {
            number.append(digit);
        }
        return digitsOf(new BigInteger(number.toString()).add(BigInteger.ONE).toString());
    }

    @Test(order = 1)
    void sample() {
        expect(new int[] {5, 8, 0, 7}, new int[] {5, 8, 0, 6});
        expect(new int[] {4, 0, 0, 0}, new int[] {3, 9, 9, 9});
        expect(new int[] {1, 0, 0, 0, 0, 0, 0}, new int[] {9, 9, 9, 9, 9, 9});
    }

    @Test(order = 2)
    void singleDigits() {
        expect(new int[] {1}, new int[] {0});
        expect(new int[] {6}, new int[] {5});
        expect(new int[] {4}, new int[] {3});
    }

    @Test(order = 3)
    void onlyTheLastDigitChanges() {
        expect(new int[] {7, 0, 5}, new int[] {7, 0, 4});
        expect(new int[] {6, 1, 9}, new int[] {6, 1, 8});
        expect(new int[] {2, 7}, new int[] {2, 6});
        expect(new int[] {1, 0, 0, 0, 1}, new int[] {1, 0, 0, 0, 0});
        expect(new int[] {9, 9, 9, 9}, new int[] {9, 9, 9, 8});
    }

    @Test(order = 4)
    void ninesBeforeTheEndStay() {
        // the carry stops at the first digit below 9, so the 9s to its left keep their value
        expect(new int[] {9, 5, 0}, new int[] {9, 4, 9});
        expect(new int[] {9, 9, 0, 0}, new int[] {9, 8, 9, 9});
        expect(new int[] {9, 1, 0}, new int[] {9, 0, 9});
        expect(new int[] {8, 9, 9, 9, 9, 8}, new int[] {8, 9, 9, 9, 9, 7});
        expect(new int[] {1, 9, 0, 0}, new int[] {1, 8, 9, 9});
    }

    @Test(order = 5)
    void allNinesGrowByOneDigit() {
        expect(new int[] {1, 0, 0}, new int[] {9, 9});
        // an 11-digit counter at its last value: the next one needs a twelfth digit
        int[] twelve = repeat(0, 12);
        twelve[0] = 1;
        expect(twelve, repeat(9, 11));
        int[] longest = repeat(0, 101);
        longest[0] = 1;
        expect(longest, repeat(9, 100));
    }

    @Test(order = 6)
    void numbersTooLongForALong() {
        // Long.MAX_VALUE is 9223372036854775807: one more overflows a long
        expect(digitsOf("9223372036854775808"), digitsOf("9223372036854775807"));
        expect(digitsOf("10000000000000000000"), digitsOf("9999999999999999999"));
        expect(digitsOf("4710000000000000000000000"), digitsOf("4709999999999999999999999"));
        String hundred = "3" + "0".repeat(40) + "5".repeat(58) + "2";
        expect(digitsOf(hundred.substring(0, 99) + "3"), digitsOf(hundred));
    }

    @Test(order = 7)
    void randomNumbers() {
        Random random = new Random(1447);
        for (int round = 0; round < 1000; round++) {
            int length = 1 + random.nextInt(100);
            int[] digits = new int[length];
            int nines = random.nextInt(length + 1); // a run of 9s at the end, of any length
            for (int i = 0; i < length; i++) {
                digits[i] = i >= length - nines ? 9 : random.nextInt(10);
            }
            if (length > 1 && digits[0] == 0) {
                digits[0] = 1 + random.nextInt(9);
            }
            expect(plusOneByValue(digits), digits);
        }
    }
}
