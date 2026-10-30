import static kit.Check.assertEquals;

import kit.Test;

public class Phase1Tests {

    @Test(order = 1)
    void sample() {
        int[][] links = {{0, 1}, {1, 2}, {3, 4}};
        assertEquals(true, Routes.connected(5, links, 0, 2));
        assertEquals(false, Routes.connected(5, links, 0, 4));
        assertEquals(true, Routes.connected(5, links, 4, 3));
    }

    @Test(order = 2)
    void singleStationWithNoConnectors() {
        assertEquals(true, Routes.connected(1, new int[][] {}, 0, 0));
    }

    @Test(order = 3)
    void isolatedStart() {
        int[][] links = {{1, 2}};
        assertEquals(true, Routes.connected(3, links, 0, 0));
        assertEquals(false, Routes.connected(3, links, 0, 2));
    }

    @Test(order = 4)
    void loopInTheNetwork() {
        int[][] links = {{0, 1}, {1, 2}, {2, 0}, {2, 3}};
        assertEquals(true, Routes.connected(5, links, 0, 3));
        assertEquals(false, Routes.connected(5, links, 0, 4));
    }

    @Test(order = 5)
    void connectorsWorkBothWays() {
        int[][] links = {{3, 1}};
        assertEquals(true, Routes.connected(4, links, 1, 3));
        assertEquals(true, Routes.connected(4, links, 3, 1));
    }

    @Test(order = 6)
    void repeatedConnectors() {
        int[][] links = {{0, 1}, {1, 0}, {0, 1}};
        assertEquals(true, Routes.connected(3, links, 0, 1));
        assertEquals(false, Routes.connected(3, links, 0, 2));
    }

    @Test(order = 7)
    void longDetour() {
        // 3 to 0 goes the long way round: 3, 6, 5, 4, 0
        int[][] links = {{0, 1}, {1, 2}, {2, 3}, {0, 4}, {4, 5}, {5, 6}, {6, 3}};
        assertEquals(true, Routes.connected(7, links, 3, 0));
        assertEquals(true, Routes.connected(7, links, 5, 2));
    }

    @Test(order = 8, timeoutMs = 600)
    void sixtyThousandStationsOnOneLine() {
        int n = 60_000;
        int[][] links = new int[n - 1][];
        for (int i = 0; i < n - 1; i++) {
            int k = n - 2 - i; // listed from the far end
            links[i] = new int[] {k, k + 1};
        }
        assertEquals(true, Routes.connected(n, links, 0, n - 1));
        assertEquals(true, Routes.connected(n, links, n - 1, 0));
    }
}
