import static kit.Check.assertEquals;

import java.util.Random;
import kit.Test;

public class Phase1Tests {

    private static void expect(int want, String digits) {
        assertEquals(want, Telegram.messages(digits), "digits = \"" + digits + "\"");
    }

    @Test(order = 1)
    void sample() {
        expect(4, "1726");
        expect(1, "2101");
        expect(0, "507");
    }

    @Test(order = 2)
    void aZeroCannotStandAlone() {
        expect(0, "00");
        expect(0, "0123");
        expect(0, "9001");
        expect(0, "130");
    }

    @Test(order = 3)
    void aZeroJoinsTheOneOrTwoBeforeIt() {
        expect(1, "10");
        expect(1, "20");
        expect(1, "1020");
        expect(1, "110");
    }

    @Test(order = 4)
    void pairsAboveTwentySixAreNotLetters() {
        expect(2, "26");
        expect(1, "27");
        expect(4, "2626");
        expect(1, "2829");
        expect(1, "99");
    }

    @Test(order = 5)
    void pairsFromSeventeenToNineteenAreLetters() {
        expect(2, "17");
        expect(2, "819");
    }

    @Test(order = 6)
    void aPairCannotStartWithZero() {
        // read 05 or 03 as a letter and each string seems to have more readings
        expect(1, "105");
        expect(1, "1203");
    }

    @Test(order = 7)
    void chainsWhereEveryNeighbourPairIsALetter() {
        expect(5, "1212");
        expect(8, "22222");
    }

    @Test(order = 8)
    void randomShortStrings() {
        Random random = new Random(1026);
        String pool = "00111222234567"; // zeros, ones and twos often, so pairs and dead ends are common
        for (int round = 0; round < 200; round++) {
            StringBuilder digits = new StringBuilder();
            int length = 1 + random.nextInt(14);
            for (int i = 0; i < length; i++) {
                digits.append(pool.charAt(random.nextInt(pool.length())));
            }
            expect(everyCut(digits.toString()), digits.toString());
        }
    }

    /** Tries every set of cut points and counts those whose pieces are all letters. */
    private static int everyCut(String digits) {
        int n = digits.length();
        int found = 0;
        for (int cuts = 0; cuts < 1 << (n - 1); cuts++) { // bit i: a cut after digit i
            boolean ok = true;
            int start = 0;
            for (int i = 0; i < n && ok; i++) {
                if (i == n - 1 || (cuts >> i & 1) == 1) {
                    String piece = digits.substring(start, i + 1);
                    ok = piece.length() <= 2 && piece.charAt(0) != '0' && Integer.parseInt(piece) <= 26;
                    start = i + 1;
                }
            }
            if (ok) {
                found++;
            }
        }
        return found;
    }

    @Test(order = 9, timeoutMs = 200)
    void aLongChainWithTheLargestAnswer() {
        // 45 digits where every neighbour pair is a letter read like Fibonacci: F(46) ways;
        // the nines cannot pair with anything, so they add no choices
        expect(1_836_311_903, "21".repeat(22) + "2" + "9".repeat(55));
    }

    @Test(order = 10, timeoutMs = 200)
    void aLongChainThatEndsInADeadEnd() {
        // the final 0 cannot stand alone and 50 is not a letter, so no reading survives
        expect(0, "12".repeat(49) + "50");
    }
}
