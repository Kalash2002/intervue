import static kit.Check.assertEquals;

import java.util.Arrays;
import java.util.Random;
import kit.Test;

public class Phase1Tests {

    @Test(order = 1)
    void sample() {
        assertEquals(12, RingRoad.maxLoot(new int[] {9, 2, 3, 8}));
        assertEquals(17, RingRoad.maxLoot(new int[] {2, 7, 5, 1, 6, 9}));
        assertEquals(5, RingRoad.maxLoot(new int[] {5}));
    }

    @Test(order = 2)
    void oneHouseIsTakenEvenThoughTheRoadIsACircle() {
        assertEquals(5, RingRoad.maxLoot(new int[] {5}), "a single house has no neighbour");
        assertEquals(0, RingRoad.maxLoot(new int[] {0}));
    }

    @Test(order = 3)
    void twoHousesAreNeighbours() {
        assertEquals(8, RingRoad.maxLoot(new int[] {3, 8}));
        assertEquals(8, RingRoad.maxLoot(new int[] {8, 3}));
    }

    @Test(order = 4)
    void threeHousesAreAllNeighbours() {
        // on a straight street 7 + 6 would be allowed; on the circle houses 0 and 2 touch
        assertEquals(7, RingRoad.maxLoot(new int[] {7, 2, 6}));
    }

    @Test(order = 5)
    void theFirstAndLastHouseTouch() {
        assertEquals(11, RingRoad.maxLoot(new int[] {10, 1, 1, 10}), "only one of the two 10s");
    }

    @Test(order = 6)
    void theBestChoiceMayUseTheLastHouse() {
        // dropping only the last house gives 6; the answer takes houses 1 and 4
        assertEquals(14, RingRoad.maxLoot(new int[] {1, 5, 1, 1, 9}));
    }

    @Test(order = 7)
    void randomShortCircles() {
        Random random = new Random(4242);
        for (int round = 0; round < 200; round++) {
            int[] cash = new int[1 + random.nextInt(14)];
            for (int i = 0; i < cash.length; i++) {
                cash[i] = random.nextInt(50);
            }
            assertEquals(everyChoice(cash), RingRoad.maxLoot(cash), "cash = " + Arrays.toString(cash));
        }
    }

    /** Tries every set of houses with no two neighbours on the circle. */
    private static int everyChoice(int[] cash) {
        int n = cash.length;
        int best = 0;
        for (int set = 0; set < 1 << n; set++) {
            boolean firstAndLast = n > 1 && (set & 1) == 1 && (set >> (n - 1) & 1) == 1;
            if ((set & (set >> 1)) != 0 || firstAndLast) {
                continue; // two neighbours in the set
            }
            int total = 0;
            for (int i = 0; i < n; i++) {
                if ((set >> i & 1) == 1) {
                    total += cash[i];
                }
            }
            best = Math.max(best, total);
        }
        return best;
    }

    @Test(order = 8, timeoutMs = 1000)
    void aLongRoadAtTheLargestAmounts() {
        int[] cash = new int[1000];
        Arrays.fill(cash, 1_000_000);
        assertEquals(500_000_000, RingRoad.maxLoot(cash), "every other house around the circle");
    }

    @Test(order = 9, timeoutMs = 1000)
    void aLongRoadWhoseEndsBothHoldFives() {
        // 5, 1, 1 repeated, then a final 5 that touches house 0: one 5 must go, and a 1 joins
        int[] cash = new int[1000];
        for (int i = 0; i < cash.length; i++) {
            cash[i] = i % 3 == 0 ? 5 : 1;
        }
        assertEquals(1666, RingRoad.maxLoot(cash));
    }
}
