import static kit.Check.assertEquals;

import kit.Test;

public class Phase1Tests {

    private static void expect(int window, int[] calls, int[] expected) {
        CallCounter counter = new CallCounter(window);
        for (int i = 0; i < calls.length; i++) {
            assertEquals(expected[i], counter.ping(calls[i]), "call at " + calls[i]);
        }
    }

    @Test(order = 1)
    void sample() {
        expect(10, new int[] {4, 9, 14, 15, 25}, new int[] {1, 2, 3, 3, 2});
    }

    @Test(order = 2)
    void firstCallCountsItself() {
        assertEquals(1, new CallCounter(7).ping(1));
        assertEquals(1, new CallCounter(1).ping(1_000_000_000));
    }

    @Test(order = 3)
    void windowOfOne() {
        expect(1, new int[] {1, 2, 3}, new int[] {1, 2, 2});
        expect(1, new int[] {1, 3, 4}, new int[] {1, 1, 2});
    }

    @Test(order = 4)
    void callExactlyOneWindowAgoStillCounts() {
        expect(5, new int[] {10, 15, 16}, new int[] {1, 2, 2});
    }

    @Test(order = 5)
    void shortGapsAndLongGaps() {
        expect(5, new int[] {100, 103, 108, 109}, new int[] {1, 2, 2, 2});
        expect(5, new int[] {1, 2, 3, 4, 5, 6, 7}, new int[] {1, 2, 3, 4, 5, 6, 6});
    }

    @Test(order = 6)
    void everythingDropsOutAfterALongPause() {
        expect(10, new int[] {1, 2, 3, 500}, new int[] {1, 2, 3, 1});
    }

    @Test(order = 7)
    void hugeWindowKeepsEverything() {
        expect(1_000_000_000, new int[] {1, 2, 1_000_000_000}, new int[] {1, 2, 3});
        expect(999_999_999, new int[] {1, 1_000_000_000}, new int[] {1, 2});
        expect(999_999_998, new int[] {1, 1_000_000_000}, new int[] {1, 1});
    }

    @Test(order = 8)
    void countersAreIndependent() {
        CallCounter a = new CallCounter(3);
        CallCounter b = new CallCounter(100);
        assertEquals(1, a.ping(10));
        assertEquals(1, b.ping(10));
        assertEquals(1, a.ping(20));
        assertEquals(2, b.ping(20));
    }

    @Test(order = 9, timeoutMs = 1000)
    void twoHundredThousandCallsWithAWideWindow() {
        CallCounter counter = new CallCounter(100_000);
        for (int t = 1; t <= 200_000; t++) {
            // the range holds 100,001 whole milliseconds once t is past the window, one call each
            assertEquals(Math.min(t, 100_001), counter.ping(t), "call at " + t);
        }
    }
}
