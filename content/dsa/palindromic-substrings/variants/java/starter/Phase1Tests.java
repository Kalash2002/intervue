import static kit.Check.assertEquals;

import java.util.Random;
import kit.Test;

public class Phase1Tests {

    private static void expect(int want, String s) {
        String input = s.length() <= 40 ? "s = \"" + s + "\"" : "s of length " + s.length();
        assertEquals(want, PalindromeTally.count(s), input);
    }

    @Test(order = 1)
    void sample() {
        expect(7, "tatty");
        expect(9, "wowow");
        expect(1, "q");
    }

    @Test(order = 2)
    void evenLengthPalindromes() {
        expect(6, "xyyx"); // four letters, yy, xyyx
        expect(9, "mnnnm"); // five letters, nn twice, nnn, mnnnm
    }

    @Test(order = 3)
    void equalLettersAtDifferentPositionsCountSeparately() {
        expect(3, "pp"); // p, p, pp
        expect(9, "xxyxx"); // five letters, xx twice, xyx, xxyxx
    }

    @Test(order = 4)
    void everySingleLetterCounts() {
        expect(6, "qwerty");
    }

    @Test(order = 5)
    void randomShortStrings() {
        Random random = new Random(647);
        for (int round = 0; round < 300; round++) {
            String alphabet = random.nextBoolean() ? "ab" : "abc";
            StringBuilder s = new StringBuilder();
            int length = 1 + random.nextInt(30);
            for (int i = 0; i < length; i++) {
                s.append(alphabet.charAt(random.nextInt(alphabet.length())));
            }
            expect(everySubstring(s.toString()), s.toString());
        }
    }

    /** Reverses every substring and counts those equal to their reverse. */
    private static int everySubstring(String s) {
        int found = 0;
        for (int i = 0; i < s.length(); i++) {
            for (int j = i + 1; j <= s.length(); j++) {
                String piece = s.substring(i, j);
                if (new StringBuilder(piece).reverse().toString().equals(piece)) {
                    found++;
                }
            }
        }
        return found;
    }

    @Test(order = 6, timeoutMs = 1000)
    void oneRepeatedLetter() {
        // every one of the 5000 * 5001 / 2 substrings is a palindrome
        expect(12_502_500, "e".repeat(5000));
    }

    @Test(order = 7, timeoutMs = 1000)
    void twoLettersTakingTurns() {
        // exactly the substrings of odd length: 5000 + 4998 + ... + 2
        expect(6_252_500, "ab".repeat(2500));
    }

    @Test(order = 8, timeoutMs = 1000)
    void twoLongRunsSplitInTheMiddle() {
        // 2499 * 2500 / 2 in each run, plus the b and the c on their own
        String run = "a".repeat(2499);
        expect(6_247_502, run + "bc" + run);
    }
}
