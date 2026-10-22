import static kit.Check.assertEquals;

import java.util.Arrays;
import java.util.Random;
import kit.Test;

public class Phase1Tests {

    private static void expect(boolean want, int... weights) {
        String input = weights.length <= 40 ? Arrays.toString(weights) : weights.length + " items";
        assertEquals(want, Packs.canBalance(weights.clone()), "weights = " + input);
    }

    @Test(order = 1)
    void sample() {
        expect(true, 3, 8, 4, 6, 1);
        expect(true, 6, 11, 4, 9, 8);
        expect(false, 3, 2, 6);
        expect(false, 6);
    }

    @Test(order = 2)
    void anOddTotalHasNoHalf() {
        expect(false, 7, 2, 5, 1); // 15: a search for 7, rounded down, would find the 7
        expect(false, 5, 4);
    }

    @Test(order = 3)
    void handingItemsOutHeaviestFirstCanFail() {
        // 4 + 4 + 4 against 6 + 3 + 3; heaviest first to the lighter pack ends 13 against 11,
        // and filling one pack heaviest first stops at 6 + 4 = 10
        expect(true, 6, 4, 4, 4, 3, 3);
        expect(true, 5, 5, 4, 3, 3); // 5 + 5 against 4 + 3 + 3
    }

    @Test(order = 4)
    void eachItemGoesInOnce() {
        expect(false, 2, 9, 3); // half is 7; 2 + 2 + 3 would need the 2 twice
        expect(false, 4, 1, 7); // half is 6; 4 + 1 + 1 would need the 1 twice
    }

    @Test(order = 5)
    void equalItems() {
        expect(true, 8, 8);
        expect(true, 5, 5, 5, 5);
        expect(false, 9, 9, 9);
    }

    @Test(order = 6)
    void randomSmallLoads() {
        Random random = new Random(1110);
        for (int round = 0; round < 300; round++) {
            int[] weights = new int[1 + random.nextInt(14)];
            for (int i = 0; i < weights.length; i++) {
                weights[i] = 1 + random.nextInt(12);
            }
            expect(everyGroup(weights), weights);
        }
    }

    /** Tries every group of items for the first pack. */
    private static boolean everyGroup(int[] weights) {
        int total = Arrays.stream(weights).sum();
        for (int group = 0; group < 1 << weights.length; group++) {
            int inGroup = 0;
            for (int i = 0; i < weights.length; i++) {
                if ((group >> i & 1) == 1) {
                    inGroup += weights[i];
                }
            }
            if (2 * inGroup == total) {
                return true;
            }
        }
        return false;
    }

    @Test(order = 7, timeoutMs = 500)
    void twoHundredItemsThatMissTheHalfByAThird() {
        // 199 multiples of 3 and a 100, so the total is 1 more than a multiple of 3 and its half
        // is 2 more than one; every group weighs a multiple of 3, or one more if it holds the 100
        Random random = new Random(3_100);
        int[] weights = new int[200];
        int total = 100;
        for (int i = 0; i < 199; i++) {
            weights[i] = 3 * (1 + random.nextInt(33));
            total += weights[i];
        }
        weights[199] = 100;
        if (total % 2 != 0) {
            weights[0] += weights[0] == 99 ? -3 : 3;
        }
        expect(false, weights);
    }

    @Test(order = 8, timeoutMs = 500)
    void twoHundredEvenItemsWithAnOddHalf() {
        // even weights from 80 to 100, so the table runs close to the largest total, 20000
        Random random = new Random(2_200);
        int[] weights = new int[200];
        int total = 0;
        for (int i = 0; i < 200; i++) {
            weights[i] = 2 * (40 + random.nextInt(11));
            total += weights[i];
        }
        if (total % 4 == 0) {
            weights[0] += weights[0] == 100 ? -2 : 2; // now the total is 2 more than a multiple of 4
        }
        expect(false, weights);
    }

    @Test(order = 9, timeoutMs = 500)
    void twoHundredItemsThatBalanceOnlyFiftyFifty() {
        // 100 items of 97 and 100 of 3 weigh 10000. A pack of 5000 needs 50 of each: 97a + 3b =
        // 5000 makes a 2 more than a multiple of 3, b <= 100 makes a >= 49, and 97a <= 5000
        // makes a <= 51. Taking as many 97s as fit, 51, leaves 53, which no threes make.
        int[] weights = new int[200];
        for (int i = 0; i < 200; i++) {
            weights[i] = i < 100 ? 97 : 3;
        }
        expect(true, weights);
    }
}
