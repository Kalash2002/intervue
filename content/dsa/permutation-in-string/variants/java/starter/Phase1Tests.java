import static kit.Check.assertEquals;

import kit.Test;

public class Phase1Tests {

    @Test(order = 1)
    void sample() {
        assertEquals(true, Scramble.appearsIn("lap", "xpalq"));
    }

    @Test(order = 2)
    void oneLetterWord() {
        assertEquals(true, Scramble.appearsIn("a", "a"));
        assertEquals(true, Scramble.appearsIn("c", "abc"));
        assertEquals(false, Scramble.appearsIn("z", "abc"));
    }

    @Test(order = 3)
    void wordLongerThanText() {
        assertEquals(false, Scramble.appearsIn("abc", "ab"));
        assertEquals(false, Scramble.appearsIn("a", ""));
    }

    @Test(order = 4)
    void matchAtTheStart() {
        assertEquals(true, Scramble.appearsIn("cab", "bacxyz"));
    }

    @Test(order = 5)
    void matchAtTheVeryEnd() {
        assertEquals(true, Scramble.appearsIn("ab", "xxxba"));
    }

    @Test(order = 6)
    void sameLettersWrongCounts() {
        assertEquals(false, Scramble.appearsIn("aab", "xabbx"));
        assertEquals(false, Scramble.appearsIn("zz", "azbz"));
    }

    @Test(order = 7)
    void repeatedLettersThatMatch() {
        assertEquals(true, Scramble.appearsIn("aabb", "bbaabb"));
        assertEquals(true, Scramble.appearsIn("stop", "xxpotsy"));
    }

    @Test(order = 8)
    void lettersInReversedOrder() {
        assertEquals(true, Scramble.appearsIn("abcd", "ddcbax"));
    }

    @Test(order = 9, timeoutMs = 1000)
    void largeAlmostAllOneLetter() {
        String word = "a".repeat(49_999) + "b";
        assertEquals(false, Scramble.appearsIn(word, "a".repeat(100_000)));
    }

    @Test(order = 10, timeoutMs = 1000)
    void largeMatchInTheMiddle() {
        String word = "a".repeat(25_000) + "b".repeat(25_000);
        String text = "a".repeat(50_000) + "b".repeat(50_000);
        assertEquals(true, Scramble.appearsIn(word, text));
    }
}
