import static kit.Check.assertEquals;

import kit.Test;

public class Phase1Tests {

    @Test(order = 1)
    void sample() {
        assertEquals(5, Streaks.longestDistinct("kayakxyz"));
    }

    @Test(order = 2)
    void emptyAndSingle() {
        assertEquals(0, Streaks.longestDistinct(""));
        assertEquals(1, Streaks.longestDistinct("q"));
    }

    @Test(order = 3)
    void sameCharacterRepeated() {
        assertEquals(1, Streaks.longestDistinct("bbbbb"));
    }

    @Test(order = 4)
    void allDifferent() {
        assertEquals(6, Streaks.longestDistinct("abcdef"));
    }

    @Test(order = 5)
    void repeatAtBothEnds() {
        assertEquals(3, Streaks.longestDistinct("xyzzyx"));
    }

    @Test(order = 6)
    void earlierCopyLiesBeforeTheStart() {
        assertEquals(2, Streaks.longestDistinct("abba"));
    }

    @Test(order = 7)
    void slideRatherThanRestart() {
        assertEquals(4, Streaks.longestDistinct("xyxzw"));
    }

    @Test(order = 8)
    void caseMatters() {
        assertEquals(2, Streaks.longestDistinct("aAaA"));
        assertEquals(4, Streaks.longestDistinct("abAB"));
    }

    @Test(order = 9)
    void anyCharacterCounts() {
        assertEquals(3, Streaks.longestDistinct("a中a中b"));
        assertEquals(4, Streaks.longestDistinct("é€中Ａé"));
    }

    @Test(order = 10, timeoutMs = 1000)
    void largeRepeatingWindow() {
        char[] chars = new char[100_000];
        for (int i = 0; i < chars.length; i++) {
            chars[i] = (char) (256 + i % 50_000); // every 50,000 characters in a row are different
        }
        assertEquals(50_000, Streaks.longestDistinct(new String(chars)));
    }

    @Test(order = 11, timeoutMs = 1000)
    void largeAllDifferent() {
        char[] chars = new char[50_000];
        for (int i = 0; i < chars.length; i++) {
            chars[i] = (char) (256 + i);
        }
        assertEquals(50_000, Streaks.longestDistinct(new String(chars)));
    }
}
