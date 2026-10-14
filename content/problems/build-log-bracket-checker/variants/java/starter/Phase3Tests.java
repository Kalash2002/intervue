import static kit.Check.assertEquals;
import static kit.Check.assertFalse;

import java.util.List;
import java.util.Optional;
import kit.Test;

/** Huge lines. Each test has a time limit; a correct but slow solution fails with TIMEOUT. */
public class Phase3Tests {

    /** Two million characters nested a million deep, with one stray closer at the very end. */
    @Test(order = 1, timeoutMs = 3000)
    void aLineNestedAMillionDeep() {
        String line = Datasets.deeplyNested(1_000_000);
        BracketChecker checker = new BracketChecker();
        assertEquals(Optional.of(new BracketError(2_000_000, BracketError.Kind.UNEXPECTED_CLOSER)),
                checker.firstError(line));
        assertFalse(checker.isBalanced(line), "the stray closer");
    }

    /** 200,000 ordinary lines, a few of them broken. */
    @Test(order = 2, timeoutMs = 3000)
    void aLongBuildLog() {
        List<Integer> bad = new BracketChecker().unbalancedLines(Datasets.buildLog(200_000, 61L));
        long sum = 0;
        for (int n : bad) {
            sum += n;
        }
        assertEquals(EXPECTED_BAD_LINES, bad.size(), "unbalanced lines");
        assertEquals(EXPECTED_SUM, sum, "sum of their numbers");
    }

    static final int EXPECTED_BAD_LINES = 12_064;
    static final long EXPECTED_SUM = 1_207_042_853L;
}
