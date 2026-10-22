import static kit.Check.assertEquals;

import java.util.Random;
import kit.Test;

public class Phase1Tests {

    private static void expect(int want, String text, String word) {
        String input = "text = \"" + abbreviate(text) + "\", word = \"" + abbreviate(word) + "\"";
        assertEquals(want, Strikeout.ways(text, word), input);
    }

    private static String abbreviate(String s) {
        return s.length() <= 40 ? s : s.substring(0, 20) + "... (" + s.length() + " letters)";
    }

    @Test(order = 1)
    void sample() {
        expect(3, "tomatoes", "toes");
        expect(8, "committee", "mite");
        expect(0, "night", "thing");
    }

    @Test(order = 2)
    void aRepeatedLetterOfTheWordTakesDifferentCopies() {
        // f then two different e's after it; reusing one copy of a letter for two places counts 6
        expect(2, "coffee", "fee");
        // the first s, two different e's, then an n after both; filling both e's with one copy counts 8
        expect(3, "senescence", "seen");
    }

    @Test(order = 3)
    void aOneLetterWord() {
        expect(2, "tomatoes", "o");
    }

    @Test(order = 4)
    void theWholeTextIsOneWay() {
        expect(1, "quartz", "quartz");
    }

    @Test(order = 5)
    void aWordLongerThanTheText() {
        expect(0, "kiln", "kilns");
    }

    @Test(order = 6)
    void noLetterInCommon() {
        expect(0, "lynx", "cow");
    }

    @Test(order = 7)
    void randomShortTextsAgainstEveryChoiceOfPositions() {
        Random random = new Random(1123);
        for (int round = 0; round < 300; round++) {
            String letters = round % 2 == 0 ? "ab" : "abc";
            String text = word(random, letters, 1 + random.nextInt(12));
            String word = word(random, letters, 1 + random.nextInt(5));
            expect(everyChoice(text, word), text, word);
        }
    }

    private static String word(Random random, String letters, int length) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < length; i++) {
            s.append(letters.charAt(random.nextInt(letters.length())));
        }
        return s.toString();
    }

    /** Tries every set of word.length() positions of text and counts the sets that spell word. */
    private static int everyChoice(String text, String word) {
        int count = 0;
        for (int keep = 0; keep < 1 << text.length(); keep++) {
            if (Integer.bitCount(keep) != word.length()) {
                continue;
            }
            StringBuilder kept = new StringBuilder();
            for (int i = 0; i < text.length(); i++) {
                if ((keep >> i & 1) == 1) {
                    kept.append(text.charAt(i));
                }
            }
            if (kept.toString().equals(word)) {
                count++;
            }
        }
        return count;
    }

    @Test(order = 8, timeoutMs = 500)
    void oneWayWhileShorterBeginningsRunPastEveryIntegerType() {
        // all 300 a's before the b must be kept; the counts for spelling the first a's of the word
        // from longer stretches of the text run past 2^63
        expect(1, "a".repeat(300) + "b" + "a".repeat(699), "a".repeat(300) + "b");
    }

    @Test(order = 9, timeoutMs = 500)
    void blocksOfFiftyWithFiveLettersShort() {
        // 20 blocks of 50 copies of one letter, a to t; the word keeps every block whole except
        // five, which are one letter short, so each of those drops one of 50 copies: 50^5 ways
        StringBuilder text = new StringBuilder();
        StringBuilder word = new StringBuilder();
        for (char letter = 'a'; letter <= 't'; letter++) {
            text.append(String.valueOf(letter).repeat(50));
            word.append(String.valueOf(letter).repeat("cfjmr".indexOf(letter) >= 0 ? 49 : 50));
        }
        expect(312_500_000, text.toString(), word.toString());
    }

    @Test(order = 10, timeoutMs = 500)
    void everyAThenTheLastB() {
        // the word needs all 500 a's, so the only b after the last of them is the final letter
        expect(1, "ab".repeat(500), "a".repeat(500) + "b");
    }
}
