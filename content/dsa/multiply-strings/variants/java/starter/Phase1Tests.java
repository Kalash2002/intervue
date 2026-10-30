import static kit.Check.assertEquals;
import static kit.Check.assertTrue;

import java.math.BigInteger;
import java.util.Random;
import kit.Test;

public class Phase1Tests {

    private static String show(String s) {
        return s.length() <= 24 ? "\"" + s + "\"" : "a " + s.length() + "-digit number starting " + s.substring(0, 8);
    }

    private static void expect(String want, String num1, String num2) {
        String call = "multiply(" + show(num1) + ", " + show(num2) + ")";
        String got = Multiplier.multiply(num1, num2);
        assertTrue(got != null, call + " returned null");
        if (!got.equals(want)) { // quoted, so that "" and "0" read differently
            assertEquals("\"" + want + "\"", "\"" + got + "\"", call);
        }
    }

    /** A number of the given length with no leading zero, from a seeded generator. */
    private static String digits(Random random, int length) {
        StringBuilder s = new StringBuilder();
        s.append((char) ('1' + random.nextInt(9)));
        for (int i = 1; i < length; i++) {
            s.append((char) ('0' + random.nextInt(10)));
        }
        return s.toString();
    }

    @Test(order = 1)
    void sample() {
        expect("3010560", "4096", "735");
        expect("999890001", "99999", "9999");
        expect("0", "0", "58173");
    }

    @Test(order = 2)
    void aZeroOperand() {
        expect("0", "0", "0");
        expect("0", "7300", "0");
        expect("0", "0", "8164");
        expect("0", "6" + "0".repeat(150) + "4" + "2".repeat(48), "0");
    }

    @Test(order = 3)
    void productsWithFewerDigitsThanTheOperands() {
        // the product of an m-digit and an n-digit number can have m + n - 1 digits: no leading zero may be left
        expect("90", "15", "6");
        expect("1111", "101", "11");
        expect("72", "8", "9");
        expect("5", "5", "1");
        expect("1", "1", "1");
        // zeros inside and at the end of the product stay
        expect("100", "25", "4");
        expect("1000", "125", "8");
        expect("10000", "250", "40");
        expect("4002", "138", "29");
    }

    @Test(order = 4)
    void carriesThroughLongNumbers() {
        expect("8" + "9".repeat(199) + "1", "9".repeat(200), "9");
        expect("2".repeat(200) + "0", "5".repeat(200), "4");
        expect("98" + "9".repeat(38) + "01", "9".repeat(40), "99");
        expect("1".repeat(120) + "00", "4".repeat(120), "25");
        expect("1" + "0".repeat(200), "1" + "0".repeat(199), "10");
    }

    @Test(order = 5)
    void oneAndLongOperands() {
        String long1 = "3" + "1".repeat(98) + "7" + "5".repeat(99) + "8";
        expect(long1, long1, "1");
        expect("700000", "7", "100000");
        expect("1000000", "1000", "1000");
    }

    @Test(order = 6, timeoutMs = 500)
    void bothOperandsLong() {
        String nines = "9".repeat(200);
        expect("9".repeat(199) + "8" + "0".repeat(199) + "1", nines, nines);
        String tenPower = "1" + "0".repeat(199);
        expect("1" + "0".repeat(398), tenPower, tenPower);
        expect("0", "0", nines);
        String long2 = "2" + "8".repeat(120) + "0".repeat(60) + "6".repeat(18);
        expect(long2, "1", long2);
        Random random = new Random(1423);
        String a = digits(random, 200);
        String b = digits(random, 200);
        String product = new BigInteger(a).multiply(new BigInteger(b)).toString();
        expect(product, a, b);
        expect(product, b, a);
    }

    @Test(order = 7, timeoutMs = 1000)
    void randomPairs() {
        Random random = new Random(1427);
        for (int round = 0; round < 300; round++) {
            String a = random.nextInt(25) == 0 ? "0" : digits(random, 1 + random.nextInt(200));
            String b = random.nextInt(25) == 0 ? "0" : digits(random, 1 + random.nextInt(200));
            // the test's own answer comes from BigInteger, which the solution may not use
            expect(new BigInteger(a).multiply(new BigInteger(b)).toString(), a, b);
        }
    }
}
