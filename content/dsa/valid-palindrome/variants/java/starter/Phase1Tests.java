import static kit.Check.assertEquals;

import java.util.Random;
import kit.Test;

public class Phase1Tests {

    @Test(order = 1)
    void sample() {
        assertEquals(true, Palindromes.isPalindrome("Never odd, or even!"));
    }

    @Test(order = 2)
    void emptyString() {
        assertEquals(true, Palindromes.isPalindrome(""));
    }

    @Test(order = 3)
    void onlyPunctuationAndSpaces() {
        assertEquals(true, Palindromes.isPalindrome(" .,; "));
        assertEquals(true, Palindromes.isPalindrome("?!"));
    }

    @Test(order = 4)
    void singleCharacter() {
        assertEquals(true, Palindromes.isPalindrome("x"));
        assertEquals(true, Palindromes.isPalindrome("-x-"));
    }

    @Test(order = 5)
    void mismatches() {
        assertEquals(false, Palindromes.isPalindrome("ab"));
        assertEquals(false, Palindromes.isPalindrome("fast car"));
        assertEquals(false, Palindromes.isPalindrome("a, b, c"));
    }

    @Test(order = 6)
    void digitsAreCompared() {
        assertEquals(false, Palindromes.isPalindrome("a7"));
        assertEquals(false, Palindromes.isPalindrome("1b2"));
        assertEquals(true, Palindromes.isPalindrome("12 b 21"));
        assertEquals(true, Palindromes.isPalindrome("7b7"));
    }

    @Test(order = 7)
    void caseIsIgnored() {
        assertEquals(true, Palindromes.isPalindrome("AbBa"));
        assertEquals(false, Palindromes.isPalindrome("Ab"));
    }

    @Test(order = 8)
    void punctuationBetweenAndAround() {
        assertEquals(true, Palindromes.isPalindrome("a.,b;;b ?A"));
        assertEquals(true, Palindromes.isPalindrome("..ab,,a!!"));
        assertEquals(false, Palindromes.isPalindrome("..ab,,c!!"));
    }

    private static String sprinkled(char[] half, boolean breakMiddle) {
        Random random = new Random(9);
        String punctuation = " ,.;!?-";
        StringBuilder sb = new StringBuilder();
        int n = half.length;
        for (int i = 0; i < 2 * n; i++) {
            char c = i < n ? half[i] : half[2 * n - 1 - i];
            if (i == n && breakMiddle) {
                c = half[n - 1] == '0' ? '1' : '0'; // differs from its partner at position n - 1
            }
            sb.append(c);
            if (random.nextInt(5) == 0) {
                sb.append(punctuation.charAt(random.nextInt(punctuation.length())));
            }
        }
        return sb.toString();
    }

    private static char[] half() {
        Random random = new Random(4);
        char[] half = new char[200_000];
        for (int i = 0; i < half.length; i++) {
            int k = random.nextInt(36);
            half[i] = k < 26 ? (char) (random.nextBoolean() ? 'a' + k : 'A' + k) : (char) ('0' + k - 26);
        }
        return half;
    }

    @Test(order = 9, timeoutMs = 1000)
    void largePalindrome() {
        assertEquals(true, Palindromes.isPalindrome(sprinkled(half(), false)));
    }

    @Test(order = 10, timeoutMs = 1000)
    void largeWithOneMismatch() {
        assertEquals(false, Palindromes.isPalindrome(sprinkled(half(), true)));
    }
}
