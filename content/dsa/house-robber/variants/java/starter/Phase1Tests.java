import static kit.Check.assertEquals;

import java.util.Arrays;
import java.util.Random;
import kit.Test;

public class Phase1Tests {

    @Test(order = 1)
    void sample() {
        assertEquals(15, Street.maxLoot(new int[] {6, 1, 2, 9, 4}));
        assertEquals(18, Street.maxLoot(new int[] {3, 10, 3, 1, 8}));
        assertEquals(7, Street.maxLoot(new int[] {7}));
    }

    @Test(order = 2)
    void twoHousesGiveTheRicherOne() {
        assertEquals(9, Street.maxLoot(new int[] {4, 9}));
        assertEquals(9, Street.maxLoot(new int[] {9, 4}));
    }

    @Test(order = 3)
    void emptyHouses() {
        assertEquals(0, Street.maxLoot(new int[] {0}));
        assertEquals(0, Street.maxLoot(new int[] {0, 0, 0}));
    }

    @Test(order = 4)
    void evenOrOddHousesAreNotEnough() {
        // both alternating sets give 11; houses 0 and 3 give 17
        assertEquals(17, Street.maxLoot(new int[] {8, 2, 3, 9}));
    }

    @Test(order = 5)
    void theRichestHouseIsNotAlwaysTaken() {
        // taking the 6 first blocks both 5s
        assertEquals(10, Street.maxLoot(new int[] {5, 6, 5}));
    }

    @Test(order = 6)
    void randomShortStreets() {
        Random random = new Random(2026);
        for (int round = 0; round < 200; round++) {
            int[] cash = new int[1 + random.nextInt(14)];
            for (int i = 0; i < cash.length; i++) {
                cash[i] = random.nextInt(50);
            }
            assertEquals(everyChoice(cash), Street.maxLoot(cash), "cash = " + Arrays.toString(cash));
        }
    }

    /** Tries every set of houses with no two neighbours. */
    private static int everyChoice(int[] cash) {
        int best = 0;
        for (int set = 0; set < 1 << cash.length; set++) {
            if ((set & (set >> 1)) != 0) {
                continue; // two neighbours in the set
            }
            int total = 0;
            for (int i = 0; i < cash.length; i++) {
                if ((set >> i & 1) == 1) {
                    total += cash[i];
                }
            }
            best = Math.max(best, total);
        }
        return best;
    }

    @Test(order = 7, timeoutMs = 1000)
    void aLongStreetAtTheLargestAmounts() {
        int[] cash = new int[999];
        Arrays.fill(cash, 1_000_000);
        assertEquals(500_000_000, Street.maxLoot(cash), "houses 0, 2, ..., 998");
    }

    @Test(order = 8, timeoutMs = 1000)
    void aLongStreetThatSkipsTwoAtATime() {
        // 5, 1, 1 repeated, then a final 5: take every 5, which are three apart, for 334 x 5
        int[] cash = new int[1000];
        for (int i = 0; i < cash.length; i++) {
            cash[i] = i % 3 == 0 ? 5 : 1;
        }
        assertEquals(1670, Street.maxLoot(cash));
    }
}
