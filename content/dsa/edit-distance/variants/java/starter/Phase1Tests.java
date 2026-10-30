import static kit.Check.assertEquals;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import kit.Test;

public class Phase1Tests {

    private static void expect(int want, String typed, String word) {
        String input = "typed = \"" + abbreviate(typed) + "\", word = \"" + abbreviate(word) + "\"";
        assertEquals(want, Typo.fewestFixes(typed, word), input);
    }

    private static String abbreviate(String s) {
        return s.length() <= 40 ? s : s.substring(0, 20) + "... (" + s.length() + " letters)";
    }

    @Test(order = 1)
    void sample() {
        expect(2, "plaster", "pastel");
        expect(3, "sprint", "paints");
        expect(4, "", "fern");
    }

    @Test(order = 2)
    void theWordsCanComeInEitherOrder() {
        // a deletion one way is an insertion the other way, so the count is the same
        expect(2, "pastel", "plaster");
        expect(3, "paints", "sprint");
        expect(4, "fern", "");
    }

    @Test(order = 3)
    void oneMissingLetterShiftsEverythingAfterIt() {
        // position by position every letter differs, yet one insertion lines them up
        expect(1, "ought", "bought");
        // delete the t, replace the h with an n
        expect(2, "thimble", "nimble");
    }

    @Test(order = 4)
    void aReplacementIsOneFixNotTwo() {
        // with deletions and insertions only, each of these takes twice as many fixes
        expect(1, "hammock", "hummock");
        expect(2, "lemon", "melon");
    }

    @Test(order = 5)
    void emptyStrings() {
        expect(0, "", "");
        expect(4, "moss", "");
    }

    @Test(order = 6)
    void theSameWordNeedsNoFix() {
        expect(0, "velvet", "velvet");
    }

    @Test(order = 7)
    void noLetterInCommon() {
        // replace all three letters and insert two more
        expect(5, "why", "ocean");
    }

    @Test(order = 8)
    void randomShortWordsAgainstASearchOverEveryFix() {
        Random random = new Random(1191);
        for (int round = 0; round < 300; round++) {
            String letters = round % 2 == 0 ? "ab" : "abc";
            String typed = word(random, letters, random.nextInt(7));
            String word = word(random, letters, random.nextInt(7));
            expect(searchEveryFix(typed, word, letters), typed, word);
        }
    }

    private static String word(Random random, String letters, int length) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < length; i++) {
            s.append(letters.charAt(random.nextInt(letters.length())));
        }
        return s.toString();
    }

    /**
     * Breadth-first search from typed, one fix per step, over strings of the given letters. No
     * string on some shortest path is longer than the longer of the two words (do the deletions
     * first and the insertions last), and every letter inserted or replaced ends up in word.
     */
    private static int searchEveryFix(String typed, String word, String letters) {
        int longest = Math.max(typed.length(), word.length());
        Map<String, Integer> fixes = new HashMap<>();
        ArrayDeque<String> queue = new ArrayDeque<>();
        fixes.put(typed, 0);
        queue.add(typed);
        while (true) {
            String s = queue.poll();
            if (s.equals(word)) {
                return fixes.get(s);
            }
            for (String next : oneFixAway(s, letters, longest)) {
                if (fixes.putIfAbsent(next, fixes.get(s) + 1) == null) {
                    queue.add(next);
                }
            }
        }
    }

    private static List<String> oneFixAway(String s, String letters, int longest) {
        List<String> next = new ArrayList<>();
        for (int k = 0; k < s.length(); k++) {
            next.add(s.substring(0, k) + s.substring(k + 1));
            for (char c : letters.toCharArray()) {
                if (c != s.charAt(k)) {
                    next.add(s.substring(0, k) + c + s.substring(k + 1));
                }
            }
        }
        if (s.length() < longest) {
            for (int k = 0; k <= s.length(); k++) {
                for (char c : letters.toCharArray()) {
                    next.add(s.substring(0, k) + c + s.substring(k));
                }
            }
        }
        return next;
    }

    private static String letters(Random random, char from, int length) {
        char[] s = new char[length];
        for (int i = 0; i < length; i++) {
            s[i] = (char) (from + random.nextInt(13));
        }
        return new String(s);
    }

    @Test(order = 9)
    void everyPositionDiffersButTwoFixesDo() {
        // delete the first a and put an a at the end
        expect(2, "ab".repeat(250), "ba".repeat(250));
    }

    @Test(order = 10, timeoutMs = 400)
    void longWordsWithNoLetterInCommon() {
        // nothing can be kept: replace 400 letters and delete the other 100
        Random random = new Random(1919);
        expect(500, letters(random, 'a', 500), letters(random, 'n', 400));
    }

    @Test(order = 11, timeoutMs = 400)
    void everyTenthLetterReplacedByOneTheWordLacks() {
        // word uses a to m only, so each of the 50 letters from n to z costs a fix, and 50 replacements do it
        Random random = new Random(2026);
        String word = letters(random, 'a', 500);
        char[] typed = word.toCharArray();
        for (int i = 9; i < typed.length; i += 10) {
            typed[i] = (char) ('n' + random.nextInt(13));
        }
        expect(50, new String(typed), word);
    }

    @Test(order = 12, timeoutMs = 400)
    void everyTenthLetterLeftOut() {
        // typed is 50 letters shorter, so at least 50 fixes, and inserting the 50 letters does it
        Random random = new Random(1206);
        String word = letters(random, 'a', 500);
        StringBuilder typed = new StringBuilder();
        for (int i = 0; i < word.length(); i++) {
            if (i % 10 != 9) {
                typed.append(word.charAt(i));
            }
        }
        expect(50, typed.toString(), word);
    }
}
