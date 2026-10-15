import static kit.Check.assertEquals;
import static kit.Check.assertFalse;
import static kit.Check.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import kit.Test;

public class Phase1Tests {

    private final BracketChecker checker = new BracketChecker();

    @Test(order = 1)
    void balancedLinesPass() {
        assertTrue(checker.isBalanced("a(b[c]{d})e"), "nested brackets");
        assertTrue(checker.isBalanced(""), "empty line");
        assertTrue(checker.isBalanced("no brackets at all"), "no brackets");
    }

    @Test(order = 2)
    void aStrayCloserFails() {
        assertFalse(checker.isBalanced(")("), "closer first");
        assertFalse(checker.isBalanced("a)"), "closer with nothing open");
    }

    @Test(order = 3)
    void mismatchedPairsFail() {
        assertFalse(checker.isBalanced("(]"), "round closed by square");
        assertFalse(checker.isBalanced("([)]"), "crossed pairs");
    }

    @Test(order = 4)
    void anOpenerLeftOpenFails() {
        assertFalse(checker.isBalanced("(("), "two openers");
        assertFalse(checker.isBalanced("(()"), "one opener left");
        assertFalse(checker.isBalanced("{"), "single opener");
    }

    @Test(order = 5)
    void sampleLog() throws Exception {
        List<String> lines = Files.readAllLines(Path.of("data/build.log"));
        assertEquals(List.of(3, 4, 5), checker.unbalancedLines(lines));
    }
}
