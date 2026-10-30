import static kit.Check.assertEquals;

import kit.Test;

public class Phase1Tests {

    @Test(order = 1)
    void sample() {
        assertEquals(true, Brackets.isBalanced("[()]{}()"));
    }

    @Test(order = 2)
    void emptyIsBalanced() {
        assertEquals(true, Brackets.isBalanced(""));
    }

    @Test(order = 3)
    void everyKindOnItsOwn() {
        assertEquals(true, Brackets.isBalanced("()"));
        assertEquals(true, Brackets.isBalanced("[]"));
        assertEquals(true, Brackets.isBalanced("{}"));
        assertEquals(true, Brackets.isBalanced("{}[]()"));
    }

    @Test(order = 4)
    void deepNesting() {
        assertEquals(true, Brackets.isBalanced("({[[]]})"));
    }

    @Test(order = 5)
    void crossedPairs() {
        assertEquals(false, Brackets.isBalanced("[{]}"));
        assertEquals(false, Brackets.isBalanced("({)}"));
    }

    @Test(order = 6)
    void closerWithNothingOpen() {
        assertEquals(false, Brackets.isBalanced(")"));
        assertEquals(false, Brackets.isBalanced("()]"));
        assertEquals(false, Brackets.isBalanced("]["));
    }

    @Test(order = 7)
    void openerNeverClosed() {
        assertEquals(false, Brackets.isBalanced("[[("));
        assertEquals(false, Brackets.isBalanced("{[]"));
    }

    @Test(order = 8)
    void wrongKind() {
        assertEquals(false, Brackets.isBalanced("[)"));
        assertEquals(false, Brackets.isBalanced("{)"));
    }

    @Test(order = 9, timeoutMs = 2000)
    void deeplyNestedBalanced() {
        int half = 200_000;
        StringBuilder text = new StringBuilder(2 * half);
        String open = "([{";
        String close = ")]}";
        for (int i = 0; i < half; i++) {
            text.append(open.charAt(i % 3));
        }
        for (int i = half - 1; i >= 0; i--) {
            text.append(close.charAt(i % 3));
        }
        assertEquals(true, Brackets.isBalanced(text.toString()));
    }

    @Test(order = 10, timeoutMs = 2000)
    void deeplyNestedWithOneWrongCloser() {
        int half = 200_000;
        StringBuilder text = new StringBuilder(2 * half);
        for (int i = 0; i < half; i++) {
            text.append('(');
        }
        for (int i = 0; i < half - 1; i++) {
            text.append(')');
        }
        text.append(']');
        assertEquals(false, Brackets.isBalanced(text.toString()));
    }
}
