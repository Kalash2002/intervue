import static kit.Check.assertEquals;
import static kit.Check.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import kit.Test;

public class Phase1Tests {

    private static final String[] KEYS = {"", "", "abc", "def", "ghi", "jkl", "mno", "pqrs", "tuv", "wxyz"};

    /** Sorts the strings, so any order is accepted; a string listed twice stays visible. */
    private static String normal(List<String> strings) {
        List<String> sorted = new ArrayList<>(strings);
        sorted.sort(null);
        return sorted.toString();
    }

    private static void expect(String digits, String... want) {
        assertEquals(normal(List.of(want)), normal(PhoneLetters.combinations(digits)));
    }

    /** The product of the key sizes, all different and each letter on its digit's key, is all of them. */
    private static void expectAll(String digits) {
        long want = 1;
        for (char d : digits.toCharArray()) {
            want *= KEYS[d - '0'].length();
        }
        List<String> got = PhoneLetters.combinations(digits);
        assertEquals(want, (long) got.size(), "number of strings");
        Set<String> seen = new HashSet<>();
        for (String s : got) {
            assertEquals(digits.length(), s.length(), "string " + s + " has the wrong length");
            for (int i = 0; i < s.length(); i++) {
                assertTrue(KEYS[digits.charAt(i) - '0'].indexOf(s.charAt(i)) >= 0, "string " + s + " has a letter that is not on its key");
            }
            assertTrue(seen.add(s), "string " + s + " is listed twice");
        }
    }

    @Test(order = 1)
    void sample() {
        expect("58", "jt", "ju", "jv", "kt", "ku", "kv", "lt", "lu", "lv");
    }

    @Test(order = 2)
    void singleDigit() {
        expect("9", "w", "x", "y", "z");
    }

    @Test(order = 3)
    void repeatedFourLetterKey() {
        expect("77", "pp", "pq", "pr", "ps", "qp", "qq", "qr", "qs", "rp", "rq", "rr", "rs", "sp", "sq", "sr", "ss");
    }

    @Test(order = 4)
    void keysAfterTheFirstFourLetterKey() {
        expect("8", "t", "u", "v");
        expect("39", "dw", "dx", "dy", "dz", "ew", "ex", "ey", "ez", "fw", "fx", "fy", "fz");
    }

    @Test(order = 5)
    void noDigits() {
        expect("");
    }

    @Test(order = 6)
    void everyKeyOnce() {
        expectAll("2345");
        expectAll("6789");
    }

    @Test(order = 7, timeoutMs = 2000)
    void fiveDigits() {
        expectAll("97979"); // 4^5 = 1,024 strings
        expectAll("22222");
    }
}
