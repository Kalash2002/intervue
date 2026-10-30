import static kit.Check.assertEquals;

import java.util.Random;
import kit.Test;

public class Phase1Tests {

    private static void expect(int want, String first, String second) {
        String input = "first = \"" + abbreviate(first) + "\", second = \"" + abbreviate(second) + "\"";
        assertEquals(want, Drafts.longestCommon(first, second), input);
    }

    private static String abbreviate(String s) {
        return s.length() <= 40 ? s : s.substring(0, 20) + "... (" + s.length() + " letters)";
    }

    @Test(order = 1)
    void sample() {
        expect(4, "cordon", "condor");
        expect(4, "pistachio", "captain");
        expect(1, "golf", "flog");
    }

    @Test(order = 2)
    void theArgumentsCanComeInEitherOrder() {
        expect(4, "condor", "cordon");
        expect(4, "captain", "pistachio");
    }

    @Test(order = 3)
    void matchingEachLetterAsEarlyAsPossibleFallsShort() {
        // trie and ostr; taking the first copy of each letter finds only 2, whichever word leads
        expect(4, "sterile", "latrine");
        expect(4, "coaster", "orchestra");
    }

    @Test(order = 4)
    void sharedLettersInTheWrongOrderDoNotCount() {
        // the same six letters, reversed: no two of them come in the same order
        expect(1, "repaid", "diaper");
    }

    @Test(order = 5)
    void aRepeatedLetterIsMatchedOncePerCopy() {
        // ll or ma; four letters are shared, but no three in the same order
        expect(2, "llama", "mall");
    }

    @Test(order = 6)
    void randomShortPairs() {
        Random random = new Random(1112);
        for (int round = 0; round < 300; round++) {
            String letters = round % 2 == 0 ? "ab" : "abc";
            String first = word(random, letters, 1 + random.nextInt(10));
            String second = word(random, letters, 1 + random.nextInt(10));
            expect(everySubsequence(first, second), first, second);
        }
    }

    private static String word(Random random, String letters, int length) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < length; i++) {
            s.append(letters.charAt(random.nextInt(letters.length())));
        }
        return s.toString();
    }

    /** Tries every subsequence of first and keeps the longest that is also one of second. */
    private static int everySubsequence(String first, String second) {
        int best = 0;
        for (int keep = 0; keep < 1 << first.length(); keep++) {
            StringBuilder kept = new StringBuilder();
            for (int i = 0; i < first.length(); i++) {
                if ((keep >> i & 1) == 1) {
                    kept.append(first.charAt(i));
                }
            }
            if (kept.length() > best && isSubsequence(kept, second)) {
                best = kept.length();
            }
        }
        return best;
    }

    private static boolean isSubsequence(CharSequence part, String whole) {
        int found = 0;
        for (int j = 0; j < whole.length() && found < part.length(); j++) {
            if (whole.charAt(j) == part.charAt(found)) {
                found++;
            }
        }
        return found == part.length();
    }

    @Test(order = 7, timeoutMs = 500)
    void longRunsInOppositeOrders() {
        // a common subsequence cannot mix the two letters, since a comes first in one string and
        // b in the other, so the best is all 600 b's of the second
        expect(600, "a".repeat(300) + "b".repeat(700), "b".repeat(600) + "a".repeat(400));
    }

    @Test(order = 8, timeoutMs = 500)
    void everyTenthLetterReplacedByOneTheOtherLacks() {
        // the original uses a to m only and every tenth letter of the copy is n to z, so those 100
        // letters match nothing and the other 900 are a subsequence of the original
        Random random = new Random(2026);
        char[] original = new char[1000];
        for (int i = 0; i < original.length; i++) {
            original[i] = (char) ('a' + random.nextInt(13));
        }
        char[] edited = original.clone();
        for (int i = 9; i < edited.length; i += 10) {
            edited[i] = (char) ('n' + random.nextInt(13));
        }
        expect(900, new String(edited), new String(original));
    }
}
