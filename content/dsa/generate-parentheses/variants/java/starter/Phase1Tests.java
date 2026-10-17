import static kit.Check.assertEquals;
import static kit.Check.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import kit.Test;

public class Phase1Tests {

    /** Sorts the strings, so any order is accepted; a string listed twice stays visible. */
    private static String normal(List<String> strings) {
        List<String> sorted = new ArrayList<>(strings);
        sorted.sort(null);
        return sorted.toString();
    }

    private static void expect(int n, String... want) {
        assertEquals(normal(List.of(want)), normal(Parentheses.generate(n)));
    }

    private static boolean balanced(String s) {
        int depth = 0;
        for (char ch : s.toCharArray()) {
            depth += ch == '(' ? 1 : ch == ')' ? -1 : 1_000; // any other character can never balance
            if (depth < 0) {
                return false;
            }
        }
        return depth == 0;
    }

    /**
     * Counts the strings itself (a balanced string is "(" inner ")" rest, so count[k] sums
     * count[i] * count[k - 1 - i]), then checks each returned one, so the right count means all.
     */
    private static void expectAll(int n) {
        long[] count = new long[n + 1];
        count[0] = 1;
        for (int k = 1; k <= n; k++) {
            for (int i = 0; i < k; i++) {
                count[k] += count[i] * count[k - 1 - i];
            }
        }
        List<String> got = Parentheses.generate(n);
        Set<String> seen = new HashSet<>();
        for (String s : got) {
            assertTrue(s.length() == 2 * n && balanced(s), "\"" + s + "\" is not a balanced string of " + n + " pairs");
            assertTrue(seen.add(s), "\"" + s + "\" is listed twice");
        }
        assertEquals(count[n], (long) got.size(), "number of strings");
    }

    @Test(order = 1)
    void sample() {
        expect(2, "(())", "()()");
    }

    @Test(order = 2)
    void fourPairs() {
        expect(
                4,
                "(((())))", "((()()))", "((())())", "((()))()", "(()(()))", "(()()())", "(()())()",
                "(())(())", "(())()()", "()((()))", "()(()())", "()(())()", "()()(())", "()()()()");
    }

    @Test(order = 3)
    void noPairsIsTheEmptyString() {
        expect(0, "");
    }

    @Test(order = 4)
    void onePair() {
        expect(1, "()");
    }

    @Test(order = 5)
    void threePairs() {
        expectAll(3);
    }

    @Test(order = 6)
    void fiveToSeven() {
        expectAll(5);
        expectAll(6);
        expectAll(7);
    }

    @Test(order = 7, timeoutMs = 2000)
    void eightPairs() {
        expectAll(8); // 1,430 strings
    }
}
