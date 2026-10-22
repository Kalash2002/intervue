import static kit.Check.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import kit.Test;

public class Phase1Tests {

    private static void expect(boolean want, String text, String pattern) {
        String input = "text = \"" + abbreviate(text) + "\", pattern = \"" + abbreviate(pattern) + "\"";
        assertEquals(want, Stencil.matches(text, pattern), input);
    }

    private static String abbreviate(String s) {
        return s.length() <= 40 ? s : s.substring(0, 20) + "... (" + s.length() + " characters)";
    }

    @Test(order = 1)
    void sample() {
        expect(true, "toast", "to.a*st");
        expect(true, "seventeen", "s.*en");
        expect(false, "banana", "ban*");
        expect(true, "", "x*y*");
    }

    @Test(order = 2)
    void aStarredPieceCanMatchNoLetterAnywhere() {
        expect(true, "lamp", "x*lamp");
        expect(true, "lamp", "la.*mp");
        expect(true, "lamp", "lampy*");
        expect(true, "ox", "e*ok*xq*");
    }

    @Test(order = 3)
    void aStarredPieceMustLeaveLettersForWhatFollows() {
        // taking every letter it can, or only as few as it can, both fail these
        expect(true, "pinpoint", "p.*nt");
        expect(true, "beef", "be*ef");
        expect(true, "toot", "to*ot");
    }

    @Test(order = 4)
    void aStarRepeatsOnlyThePieceBeforeIt() {
        // the star is not a wildcard of its own, and it does not repeat the whole pattern before it
        expect(false, "haha", "ha*");
        expect(false, "bandana", "ban*.");
        expect(true, "tool", "to*l");
    }

    @Test(order = 5)
    void theWholeTextMustMatch() {
        expect(false, "pinpoints", "p.*nt");
        expect(false, "stars", "st.r");
        expect(false, "star", "stars");
        expect(false, "upstart", "star");
    }

    @Test(order = 6)
    void aDotIsExactlyOneLetter() {
        expect(false, "cart", "c.t");
        expect(true, "cot", "c.t");
        expect(false, "", ".");
    }

    @Test(order = 7)
    void dotStarCanMixLetters() {
        // each copy of the dot may be a different letter
        expect(true, "road", "r.*d");
        expect(true, "kayak", "k.*k");
    }

    @Test(order = 8)
    void emptyStrings() {
        expect(true, "", "");
        expect(false, "mint", "");
        expect(false, "", "q");
    }

    @Test(order = 9)
    void randomShortCasesAgainstEveryExpansion() {
        Random random = new Random(1215);
        for (int round = 0; round < 300; round++) {
            String pattern = pattern(random, 3 + random.nextInt(4));
            String text = round % 2 == 0 ? expand(random, pattern) : letters(random, 3 + random.nextInt(8));
            expect(everyExpansion(text, pattern), text, pattern);
        }
    }

    private static String pattern(Random random, int pieces) {
        StringBuilder p = new StringBuilder();
        for (int k = 0; k < pieces; k++) {
            p.append("aab.".charAt(random.nextInt(4)));
            if (random.nextBoolean()) {
                p.append('*');
            }
        }
        return p.toString();
    }

    private static String letters(Random random, int length) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < length; i++) {
            s.append(random.nextBoolean() ? 'a' : 'b');
        }
        return s.toString();
    }

    /** A text the pattern matches: each starred piece repeated 0 to 3 times, each dot a random letter. */
    private static String expand(Random random, String pattern) {
        StringBuilder s = new StringBuilder();
        for (int j = 0; j < pattern.length(); j++) {
            boolean starred = j + 1 < pattern.length() && pattern.charAt(j + 1) == '*';
            int copies = starred ? random.nextInt(4) : 1;
            for (int c = 0; c < copies; c++) {
                s.append(pattern.charAt(j) == '.' ? (random.nextBoolean() ? 'a' : 'b') : pattern.charAt(j));
            }
            if (starred) {
                j++;
            }
        }
        return s.toString();
    }

    /**
     * Tries every number of copies for each starred piece that makes the pattern as long as the
     * text, writes the pieces out that many times and compares them with the text letter by letter.
     */
    private static boolean everyExpansion(String text, String pattern) {
        List<Character> letter = new ArrayList<>();
        List<Boolean> starred = new ArrayList<>();
        for (int j = 0; j < pattern.length(); j++) {
            letter.add(pattern.charAt(j));
            starred.add(j + 1 < pattern.length() && pattern.charAt(j + 1) == '*');
            if (starred.get(starred.size() - 1)) {
                j++;
            }
        }
        return expansions(text, letter, starred, 0, new StringBuilder());
    }

    private static boolean expansions(String text, List<Character> letter, List<Boolean> starred, int k, StringBuilder written) {
        if (written.length() > text.length()) {
            return false;
        }
        if (k == letter.size()) {
            if (written.length() != text.length()) {
                return false;
            }
            for (int i = 0; i < text.length(); i++) {
                if (written.charAt(i) != '.' && written.charAt(i) != text.charAt(i)) {
                    return false;
                }
            }
            return true;
        }
        int most = starred.get(k) ? text.length() : 1;
        for (int copies = starred.get(k) ? 0 : 1; copies <= most; copies++) {
            int before = written.length();
            for (int c = 0; c < copies; c++) {
                written.append(letter.get(k));
            }
            boolean found = expansions(text, letter, starred, k + 1, written);
            written.setLength(before);
            if (found) {
                return true;
            }
        }
        return false;
    }

    // Each timed pattern fails in its middle and fits both ends of the text, so a search from the
    // front and a search from the back both meet many ways to place the pieces before the failure.

    @Test(order = 10, timeoutMs = 300)
    void aLetterTheTextLacksInTheMiddle() {
        // the text has no c; the ten b's on either side of it can sit on C(50, 10) sets of the text's b's
        expect(false, "ab".repeat(50), ".*b".repeat(10) + "c" + ".*b".repeat(10));
    }

    @Test(order = 11, timeoutMs = 300)
    void twoBsSideBySideInTheMiddle() {
        // the pattern needs a b straight after a b, and the text never has two b's together
        expect(false, "ab".repeat(50), ".*b".repeat(10) + "b" + ".*b".repeat(10));
    }

    @Test(order = 12, timeoutMs = 300)
    void twoAsOneLetterApartInTheMiddle() {
        // the pattern needs an a two letters after an a, and the text's a's are three letters apart
        expect(false, "abb".repeat(33) + "a", ".*a".repeat(10) + ".a" + ".*a".repeat(10));
    }
}
