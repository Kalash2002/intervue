import static kit.Check.assertEquals;

import kit.Test;

public class Phase1Tests {

    @Test(order = 1)
    void sample() {
        assertEquals("goa(b)c", Expression.tidy("go(a(b)c"));
        assertEquals("xyz", Expression.tidy("x)y)(z"));
        assertEquals("((a))", Expression.tidy("((a))("));
    }

    @Test(order = 2)
    void emptyAndNoBrackets() {
        assertEquals("", Expression.tidy(""));
        assertEquals("abc", Expression.tidy("abc"));
    }

    @Test(order = 3)
    void alreadyBalancedIsUnchanged() {
        assertEquals("(a)(b(c))", Expression.tidy("(a)(b(c))"));
        assertEquals("()", Expression.tidy("()"));
    }

    @Test(order = 4)
    void onlyOpenersOrOnlyClosers() {
        assertEquals("", Expression.tidy("((("));
        assertEquals("", Expression.tidy(")))"));
        assertEquals("ab", Expression.tidy("(a(b("));
    }

    @Test(order = 5)
    void closerBeforeOpener() {
        assertEquals("", Expression.tidy(")("));
        assertEquals("a", Expression.tidy(")a("));
    }

    @Test(order = 6)
    void unpairedOpenerIsTheEarlierOne() {
        // the ")" pairs with the nearest "(", so the earlier "(" is the one removed
        assertEquals("a(b)", Expression.tidy("(a(b)"));
        assertEquals("ab(c)de", Expression.tidy("a(b(c)d(e"));
    }

    @Test(order = 7)
    void unpairedCloserAfterAPair() {
        assertEquals("(a)b", Expression.tidy("(a)b)"));
        assertEquals("()()", Expression.tidy("())(()"));
    }

    @Test(order = 8)
    void lettersKeepTheirOrder() {
        assertEquals("ab(cd)ef", Expression.tidy("ab)(cd)e)f("));
    }

    @Test(order = 9, timeoutMs = 2000)
    void deeplyNestedWithStrayBrackets() {
        int depth = 150_000;
        String nested = "(".repeat(depth) + ")".repeat(depth);
        // a stray ")" in front and a stray "(" at the back are the only brackets to remove
        assertEquals(nested, Expression.tidy(")" + nested + "("));
    }
}
