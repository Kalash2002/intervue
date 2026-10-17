import static kit.Check.assertEquals;
import static kit.Check.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import kit.Test;

public class Phase1Tests {

    /** Sorts the list of ways but not inside them: the pieces keep their left-to-right order. */
    private static String normal(List<List<String>> ways) {
        List<String> parts = new ArrayList<>();
        for (List<String> w : ways) {
            parts.add(w.toString());
        }
        parts.sort(null);
        return parts.toString();
    }

    private static void expect(String s, String[][] want) {
        List<List<String>> wanted = new ArrayList<>();
        for (String[] w : want) {
            wanted.add(List.of(w));
        }
        assertEquals(normal(wanted), normal(PalindromePartition.all(s)));
    }

    private static boolean isPalindrome(String piece) {
        return new StringBuilder(piece).reverse().toString().equals(piece);
    }

    /**
     * Counts the ways itself (ways[i] cuts s[i..]), then checks that every returned way is valid and
     * listed once, so the right count means none is missing.
     */
    private static void expectAll(String s) {
        int n = s.length();
        long[] ways = new long[n + 1];
        ways[n] = 1;
        for (int i = n - 1; i >= 0; i--) {
            for (int j = i + 1; j <= n; j++) {
                if (isPalindrome(s.substring(i, j))) {
                    ways[i] += ways[j];
                }
            }
        }
        List<List<String>> got = PalindromePartition.all(s);
        Set<String> seen = new HashSet<>();
        for (List<String> w : got) {
            for (String piece : w) {
                assertTrue(!piece.isEmpty() && isPalindrome(piece), "way " + w + " has a piece that is not a palindrome");
            }
            assertEquals(s, String.join("", w), "the pieces of " + w + " do not spell s");
            assertTrue(seen.add(w.toString()), "way " + w + " is listed twice");
        }
        assertEquals(ways[0], (long) got.size(), "number of ways");
    }

    @Test(order = 1)
    void sample() {
        expect("noon", new String[][] {{"n", "o", "o", "n"}, {"n", "oo", "n"}, {"noon"}});
    }

    @Test(order = 2)
    void oneRepeatedLetter() {
        expect("ttt", new String[][] {{"t", "t", "t"}, {"t", "tt"}, {"tt", "t"}, {"ttt"}});
    }

    @Test(order = 3)
    void noLongerPalindromes() {
        expect("xyz", new String[][] {{"x", "y", "z"}});
    }

    @Test(order = 4)
    void singleLetter() {
        expect("k", new String[][] {{"k"}});
    }

    @Test(order = 5)
    void overlappingPalindromes() {
        expect("pqpq", new String[][] {{"p", "q", "p", "q"}, {"pqp", "q"}, {"p", "qpq"}});
    }

    @Test(order = 6)
    void evenAndOddLengths() {
        expect("mmom", new String[][] {{"m", "m", "o", "m"}, {"mm", "o", "m"}, {"m", "mom"}});
    }

    @Test(order = 7)
    void twelveDifferentLetters() {
        expectAll("qwertyuiopas");
    }

    @Test(order = 8)
    void twelveEqualLetters() {
        expectAll("zzzzzzzzzzzz"); // 2^11 = 2,048 ways
    }

    @Test(order = 9, timeoutMs = 2000)
    void twelveLettersOfTwoKinds() {
        Random random = new Random(74);
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < 12; i++) {
            s.append(random.nextBoolean() ? 'a' : 'b');
        }
        expectAll(s.toString());
    }
}
