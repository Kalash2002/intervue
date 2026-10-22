import static kit.Check.assertEquals;

import java.util.Random;
import kit.Test;

public class Phase1Tests {

    private static void expect(boolean want, String s) {
        String input = s.length() <= 60 ? "\"" + s + "\"" : s.length() + " characters";
        assertEquals(want, Wildcards.canBalance(s), "s = " + input);
    }

    @Test(order = 1)
    void sample() {
        expect(true, "*)(()**");
        expect(false, "*((*");
        expect(false, ")**");
    }

    @Test(order = 2)
    void starsNeedDifferentReadings() {
        expect(true, "**)(()*)"); // ( then nothing for the first two stars, ) for the last
        expect(true, "*()*))*");
    }

    @Test(order = 3)
    void aStarCannotCloseABracketOpenedAfterIt() {
        expect(false, "(*(**("); // the last ( has nothing after it
        expect(false, "**(");
    }

    @Test(order = 4)
    void aStarCannotOpenABracketClosedBeforeIt() {
        expect(false, "*))*"); // the second ) has only one star before it
        expect(false, "*)*))*");
    }

    @Test(order = 5)
    void aBracketOnItsOwn() {
        expect(false, "(");
        expect(false, ")");
    }

    @Test(order = 6)
    void onlyStars() {
        expect(true, "*");
        expect(true, "**");
        expect(true, "*****");
    }

    @Test(order = 7)
    void longRuns() {
        expect(true, "(".repeat(1_000) + "*".repeat(1_000));
        expect(false, "*".repeat(1_000) + "(".repeat(1_000));
        expect(true, "*".repeat(1_000) + ")".repeat(1_000));
        expect(false, ")".repeat(1_000) + "*".repeat(1_000));
    }

    @Test(order = 8)
    void randomStrings() {
        Random random = new Random(1429);
        for (int round = 0; round < 300; round++) {
            int length = 8 + random.nextInt(7);
            StringBuilder s = new StringBuilder();
            if (round % 2 == 0) { // a balanced string with some characters turned into stars
                length -= length % 2;
                int open = 0;
                for (int i = 0; i < length; i++) {
                    boolean close = open > 0 && (open == length - i || random.nextBoolean());
                    open += close ? -1 : 1;
                    s.append(random.nextInt(3) == 0 ? '*' : close ? ')' : '(');
                }
            } else {
                for (int i = 0; i < length; i++) {
                    s.append("()*".charAt(random.nextInt(3)));
                }
            }
            expect(everyReading(s.toString(), 0, 0), s.toString());
        }
    }

    /** Tries every reading of every star from position i on, with open brackets waiting. */
    private static boolean everyReading(String s, int i, int open) {
        if (open < 0 || open > s.length() - i) {
            return false;
        }
        if (i == s.length()) {
            return true;
        }
        char c = s.charAt(i);
        if (c != ')' && everyReading(s, i + 1, open + 1)) {
            return true;
        }
        if (c != '(' && everyReading(s, i + 1, open - 1)) {
            return true;
        }
        return c == '*' && everyReading(s, i + 1, open);
    }

    @Test(order = 9, timeoutMs = 150)
    void starsOnTheOutside() {
        // 25,000 stars, 25,000 ), 25,000 ( and 25,000 stars: the first stars must all be ( and
        // the last all ).
        String s = "*".repeat(25_000) + ")".repeat(25_000) + "(".repeat(25_000) + "*".repeat(25_000);
        expect(true, s);
    }

    @Test(order = 10, timeoutMs = 150)
    void oneCloserTooManyInTheMiddle() {
        // As above, but with a 25,001st ) at position 50,000 and one ( fewer: from the left it has
        // nothing to close; from the right everything is fine until the stars at the start.
        String s = "*".repeat(25_000) + ")".repeat(25_001) + "(".repeat(24_999) + "*".repeat(25_000);
        expect(false, s);
    }
}
