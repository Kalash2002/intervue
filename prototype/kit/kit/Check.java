package kit;

import java.util.Objects;

/** Minimal assertions. A failed check throws AssertionError with a readable message. */
public final class Check {
    private Check() {}

    public interface ThrowingRunnable {
        void run() throws Throwable;
    }

    public static void assertEquals(Object expected, Object actual) {
        assertEquals(expected, actual, null);
    }

    public static void assertEquals(Object expected, Object actual, String hint) {
        if (!Objects.equals(expected, actual)) {
            throw new AssertionError((hint == null ? "" : hint + ": ") + "expected <" + expected + "> but was <" + actual + ">");
        }
    }

    public static void assertEquals(long expected, long actual) {
        assertEquals(expected, actual, null);
    }

    public static void assertEquals(long expected, long actual, String hint) {
        if (expected != actual) {
            throw new AssertionError((hint == null ? "" : hint + ": ") + "expected <" + expected + "> but was <" + actual + ">");
        }
    }

    public static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    public static void assertFalse(boolean condition, String message) {
        if (condition) {
            throw new AssertionError(message);
        }
    }

    public static void assertThrows(Class<? extends Throwable> expected, ThrowingRunnable code) {
        try {
            code.run();
        } catch (Throwable t) {
            if (expected.isInstance(t)) {
                return;
            }
            throw new AssertionError("expected " + expected.getSimpleName() + " but got " + t, t);
        }
        throw new AssertionError("expected " + expected.getSimpleName() + " but nothing was thrown");
    }

    public static void fail(String message) {
        throw new AssertionError(message);
    }
}
