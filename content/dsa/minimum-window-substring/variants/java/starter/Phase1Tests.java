import static kit.Check.assertEquals;

import kit.Test;

public class Phase1Tests {

    @Test(order = 1)
    void sample() {
        assertEquals("cab", Cover.smallest("xaybzcaby", "abc"));
    }

    @Test(order = 2)
    void repeatedNeededLettersNeedEnoughCopies() {
        assertEquals("abba", Cover.smallest("cabbab", "aab"));
    }

    @Test(order = 3)
    void noCover() {
        assertEquals("", Cover.smallest("hello", "z"));
        assertEquals("", Cover.smallest("hello", "lll"));
    }

    @Test(order = 4)
    void needLongerThanTextOrTextEmpty() {
        assertEquals("", Cover.smallest("ab", "abc"));
        assertEquals("", Cover.smallest("", "a"));
    }

    @Test(order = 5)
    void wholeTextIsTheOnlyAnswer() {
        assertEquals("srq", Cover.smallest("srq", "qrs"));
        assertEquals("xbca", Cover.smallest("xbca", "xabc"));
    }

    @Test(order = 6)
    void singleCharacter() {
        assertEquals("a", Cover.smallest("a", "a"));
        assertEquals("q", Cover.smallest("zzqzz", "q"));
    }

    @Test(order = 7)
    void leftmostOfEqualLengthAnswers() {
        assertEquals("ab", Cover.smallest("abxba", "ab"));
    }

    @Test(order = 8)
    void uppercaseAndLowercaseAreDifferent() {
        assertEquals("aBb", Cover.smallest("AaBb", "ab"));
    }

    @Test(order = 9, timeoutMs = 1000)
    void largeNeedOfOneRepeatedLetter() {
        assertEquals("a".repeat(50_000), Cover.smallest("a".repeat(100_000), "a".repeat(50_000)));
    }

    @Test(order = 10, timeoutMs = 1000)
    void largeNoCover() {
        assertEquals("", Cover.smallest("a".repeat(100_000), "b"));
    }
}
