import static kit.Check.assertEquals;
import static kit.Check.assertTrue;

import java.util.Random;
import kit.Test;

public class Phase1Tests {

    private static void expect(String want, String s) {
        String got = PalindromeSpan.longest(s);
        String input = s.length() <= 40 ? "s = \"" + s + "\"" : "s of length " + s.length();
        assertTrue(got != null, input + ": returned null");
        assertEquals(want.length(), got.length(), input + ": length of the answer");
        if (want.length() <= 40) {
            assertEquals("\"" + want + "\"", "\"" + got + "\"", input);
        } else {
            assertTrue(want.equals(got), input + ": the right length, but not the palindrome that starts first");
        }
    }

    @Test(order = 1)
    void sample() {
        expect("qpopq", "tqpopqm");
        expect("zxz", "Abbazxz");
        expect("nun", "nunzez");
        expect("k", "k3Z");
    }

    @Test(order = 2)
    void evenLengthPalindromes() {
        expect("yzzy", "xyzzyw");
        expect("88", "88");
        expect("q11q", "q11q2");
    }

    @Test(order = 3)
    void aTieGoesToThePalindromeThatStartsFirst() {
        expect("pip", "pip8tot");
        expect("xx", "xxbyy");
        expect("7ZZ7", "7ZZ7aWWa");
    }

    @Test(order = 4)
    void uppercaseAndLowercaseAreDifferent() {
        expect("X", "XyYx");
        expect("oo", "Noon");
    }

    @Test(order = 5)
    void digitsAreCharactersToo() {
        expect("909", "a909b");
        expect("73137", "73137x");
    }

    @Test(order = 6)
    void theWholeStringCanBeTheAnswer() {
        expect("8x8", "8x8");
        expect("MmmM", "MmmM");
        expect("Q", "Q");
    }

    @Test(order = 7)
    void noTwoEqualCharacters() {
        expect("M", "Mn0pQ");
    }

    @Test(order = 8)
    void randomShortStrings() {
        Random random = new Random(1205);
        for (int round = 0; round < 300; round++) {
            String alphabet = random.nextBoolean() ? "ab" : "abc";
            StringBuilder s = new StringBuilder();
            int length = 1 + random.nextInt(30);
            for (int i = 0; i < length; i++) {
                s.append(alphabet.charAt(random.nextInt(alphabet.length())));
            }
            expect(firstLongest(s.toString()), s.toString());
        }
    }

    /** Tries every length from the longest down and, within a length, every start from the left. */
    private static String firstLongest(String s) {
        for (int length = s.length(); length > 1; length--) {
            for (int start = 0; start + length <= s.length(); start++) {
                String piece = s.substring(start, start + length);
                if (new StringBuilder(piece).reverse().toString().equals(piece)) {
                    return piece;
                }
            }
        }
        return s.substring(0, 1);
    }

    @Test(order = 9, timeoutMs = 1000)
    void oneRepeatedCharacter() {
        String s = "w".repeat(5000);
        expect(s, s);
    }

    @Test(order = 10, timeoutMs = 500)
    void twoLongRunsSplitInTheMiddle() {
        // every long substring matches from both ends until it reaches the b or the c
        String run = "a".repeat(2499);
        expect(run, run + "bc" + run);
    }

    @Test(order = 11, timeoutMs = 500)
    void aLongEvenRunBeforeAShorterOne() {
        expect("x".repeat(2500), "x".repeat(2500) + "yz" + "x".repeat(2498));
    }
}
