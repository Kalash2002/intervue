import static kit.Check.assertEquals;

import kit.Test;

public class Phase1Tests {

    @Test(order = 1)
    void sample() {
        int[][] links = {{0, 1, 3}, {1, 2, 4}, {0, 2, 9}, {2, 3, 1}};
        assertEquals(8, Signals.timeToReachAll(4, links, 0));
    }

    @Test(order = 2)
    void unreachableNode() {
        assertEquals(-1, Signals.timeToReachAll(3, new int[][] {{0, 1, 2}}, 0));
    }

    @Test(order = 3)
    void singleNode() {
        assertEquals(0, Signals.timeToReachAll(1, new int[][] {}, 0));
    }

    @Test(order = 4)
    void linksWorkOneWayOnly() {
        int[][] links = {{1, 0, 4}};
        assertEquals(-1, Signals.timeToReachAll(2, links, 0));
        assertEquals(4, Signals.timeToReachAll(2, links, 1));
    }

    @Test(order = 5)
    void sourceIsNotNodeZero() {
        int[][] links = {{3, 0, 2}, {3, 1, 5}, {1, 2, 1}, {0, 2, 7}};
        assertEquals(6, Signals.timeToReachAll(4, links, 3));
    }

    @Test(order = 6)
    void parallelLinksUseTheFastest() {
        assertEquals(4, Signals.timeToReachAll(2, new int[][] {{0, 1, 9}, {0, 1, 4}, {0, 1, 6}}, 0));
    }

    @Test(order = 7)
    void zeroDelayOnACycle() {
        assertEquals(5, Signals.timeToReachAll(3, new int[][] {{0, 1, 0}, {1, 2, 5}, {2, 0, 1}}, 0));
    }

    @Test(order = 8)
    void longCheapDetourBeatsDirectLink() {
        int[][] links = {{0, 4, 10}, {0, 1, 1}, {1, 2, 1}, {2, 3, 1}, {3, 4, 1}};
        assertEquals(4, Signals.timeToReachAll(5, links, 0));
    }

    @Test(order = 9)
    void lastNodeIsReachedByAnExpensiveDirectLink() {
        int[][] links = {{0, 1, 1}, {0, 2, 100}, {1, 2, 200}};
        assertEquals(100, Signals.timeToReachAll(3, links, 0));
    }

    @Test(order = 10, timeoutMs = 1000)
    void longChainListedBackwards() {
        int n = 60_000;
        int[][] links = new int[n - 1][];
        long expected = 0;
        for (int i = 0; i < n - 1; i++) {
            int delay = 1 + i % 3;
            links[n - 2 - i] = new int[] {i, i + 1, delay}; // the far end of the chain is listed first
            expected += delay;
        }
        assertEquals(expected, Signals.timeToReachAll(n, links, 0));
    }
}
