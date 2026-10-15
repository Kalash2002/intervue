import static kit.Check.assertEquals;

import kit.Test;

public class Phase1Tests {

    @Test(order = 1)
    void sample() {
        assertEquals(5, Relabel.longestRun("MNMNNM", 2));
    }

    @Test(order = 2)
    void emptyString() {
        assertEquals(0, Relabel.longestRun("", 3));
    }

    @Test(order = 3)
    void noChangesAllowed() {
        assertEquals(3, Relabel.longestRun("AABBBCC", 0));
        assertEquals(1, Relabel.longestRun("ABCD", 0));
    }

    @Test(order = 4)
    void moreChangesThanCharacters() {
        assertEquals(3, Relabel.longestRun("XYZ", 10));
        assertEquals(3, Relabel.longestRun("PPQ", 5));
    }

    @Test(order = 5)
    void wholeStringFitsExactly() {
        assertEquals(4, Relabel.longestRun("ABAB", 2));
    }

    @Test(order = 6)
    void wholeStringFallsOneShort() {
        assertEquals(3, Relabel.longestRun("ABAB", 1));
    }

    @Test(order = 7)
    void majorityLetterIsInTheMiddle() {
        assertEquals(6, Relabel.longestRun("CABBBBAC", 2));
    }

    @Test(order = 8)
    void bestWindowInTheMiddleOfTheString() {
        assertEquals(9, Relabel.longestRun("CCABBBBABBBBACC", 1));
    }

    @Test(order = 9, timeoutMs = 1000)
    void largeSingleLetter() {
        assertEquals(100_000, Relabel.longestRun("A".repeat(100_000), 0));
    }

    @Test(order = 10, timeoutMs = 1000)
    void largeAlternatingLetters() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 100_000; i++) {
            sb.append(i % 2 == 0 ? 'A' : 'B');
        }
        // 99,999 characters starting at index 0 hold 50,000 As and 49,999 Bs: 49,999 changes
        assertEquals(99_999, Relabel.longestRun(sb.toString(), 49_999));
    }
}
