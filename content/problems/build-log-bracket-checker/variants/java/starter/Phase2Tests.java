import static kit.Check.assertEquals;
import static kit.Check.assertFalse;
import static kit.Check.assertTrue;

import java.util.Optional;
import kit.Test;

public class Phase2Tests {

    private final BracketChecker checker = new BracketChecker();

    private static Optional<BracketError> error(int column, BracketError.Kind kind) {
        return Optional.of(new BracketError(column, kind));
    }

    @Test(order = 1)
    void balancedLinesHaveNoError() {
        assertEquals(Optional.empty(), checker.firstError("a(b[c]{d})e"));
        assertEquals(Optional.empty(), checker.firstError(""));
    }

    @Test(order = 2)
    void closerWithNothingOpen() {
        assertEquals(error(2, BracketError.Kind.UNEXPECTED_CLOSER), checker.firstError("ab)"));
    }

    @Test(order = 3)
    void closerThatDoesNotMatch() {
        assertEquals(error(2, BracketError.Kind.MISMATCH), checker.firstError("x(]"));
        assertEquals(error(2, BracketError.Kind.MISMATCH), checker.firstError("{[}]"));
    }

    @Test(order = 4)
    void theFirstErrorWins() {
        assertEquals(error(0, BracketError.Kind.UNEXPECTED_CLOSER), checker.firstError("])"));
        assertEquals(error(1, BracketError.Kind.MISMATCH), checker.firstError("(](("));
    }

    @Test(order = 5)
    void unclosedPointsAtTheEarliestOpener() {
        assertEquals(error(0, BracketError.Kind.UNCLOSED), checker.firstError("(a[b"));
        assertEquals(error(2, BracketError.Kind.UNCLOSED), checker.firstError("()(x"));
    }

    @Test(order = 6)
    void quotedTextIsIgnored() {
        assertEquals(Optional.empty(), checker.firstError("print(\"(\")"));
        assertEquals(Optional.empty(), checker.firstError("f(\"\\\")\")"));
        assertEquals(error(10, BracketError.Kind.UNEXPECTED_CLOSER), checker.firstError("say \"[x]\" )"));
    }

    @Test(order = 7)
    void aLineEndingInsideQuotes() {
        assertEquals(error(4, BracketError.Kind.UNCLOSED_STRING), checker.firstError("log(\"oops)"));
    }

    @Test(order = 8)
    void isBalancedAgreesWithFirstError() {
        assertTrue(checker.isBalanced("print(\"(\")"), "a bracket inside quotes");
        assertFalse(checker.isBalanced("log(\"oops)"), "a line ending inside quotes");
        assertFalse(checker.isBalanced("(("), "openers left open");
    }
}
