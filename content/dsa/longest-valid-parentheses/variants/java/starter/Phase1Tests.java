import static kit.Check.assertEquals;

import kit.Test;

public class Phase1Tests {

    @Test(order = 1)
    void sample() {
        assertEquals(2, Brackets.longestBalanced("(()"));
        assertEquals(4, Brackets.longestBalanced(")()())"));
        assertEquals(6, Brackets.longestBalanced("(())()("));
    }

    @Test(order = 2)
    void emptyString() {
        assertEquals(0, Brackets.longestBalanced(""));
    }

    @Test(order = 3)
    void onlyOpenersOrOnlyClosers() {
        assertEquals(0, Brackets.longestBalanced("(((("));
        assertEquals(0, Brackets.longestBalanced("))))"));
        assertEquals(0, Brackets.longestBalanced(")("));
    }

    @Test(order = 4)
    void wholeStringBalanced() {
        assertEquals(6, Brackets.longestBalanced("()(())"));
        assertEquals(6, Brackets.longestBalanced("(()())"));
    }

    @Test(order = 5)
    void unmatchedOpenerSplitsTheString() {
        assertEquals(2, Brackets.longestBalanced("()(()"));
    }

    @Test(order = 6)
    void firstOfTwoStretchesIsLonger() {
        // "(())" at 0 to 3 and "()" at 5 to 6, split by unmatched closers
        assertEquals(4, Brackets.longestBalanced("(()))())("));
    }

    @Test(order = 7)
    void nestedAndSequentialMix() {
        assertEquals(8, Brackets.longestBalanced("(()(()))"));
    }

    @Test(order = 8, timeoutMs = 200)
    void hundredThousandCharacters() {
        StringBuilder text = new StringBuilder(")");
        for (int i = 0; i < 49_999; i++) {
            text.append("()");
        }
        text.append('(');
        assertEquals(99_998, Brackets.longestBalanced(text.toString()));
    }
}
