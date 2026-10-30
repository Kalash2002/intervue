import static kit.Check.assertEquals;

import java.util.Random;
import kit.Test;

public class Phase1Tests {

    private static int[] shuffled(int[] stones, long seed) {
        Random random = new Random(seed);
        for (int i = stones.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            int swap = stones[i];
            stones[i] = stones[j];
            stones[j] = swap;
        }
        return stones;
    }

    @Test(order = 1)
    void sample() {
        assertEquals(2, LastStone.lastWeight(new int[] {6, 3, 9, 4}));
    }

    @Test(order = 2)
    void everythingCancels() {
        assertEquals(0, LastStone.lastWeight(new int[] {5, 5, 8, 2})); // 8-5=3, 5-3=2, 2 and 2 vanish
    }

    @Test(order = 3)
    void singleStone() {
        assertEquals(40, LastStone.lastWeight(new int[] {40}));
    }

    @Test(order = 4)
    void twoStones() {
        assertEquals(0, LastStone.lastWeight(new int[] {12, 12}));
        assertEquals(17, LastStone.lastWeight(new int[] {3, 20}));
    }

    @Test(order = 5)
    void allEqual() {
        assertEquals(7, LastStone.lastWeight(new int[] {7, 7, 7}));
        assertEquals(0, LastStone.lastWeight(new int[] {7, 7, 7, 7}));
    }

    @Test(order = 6)
    void remainderStaysTheHeaviest() {
        assertEquals(13, LastStone.lastWeight(new int[] {20, 3, 2, 2})); // 17, then 15, then 13
        assertEquals(1, LastStone.lastWeight(new int[] {1, 2, 4, 8, 16})); // 8, 4, 2, 1
    }

    @Test(order = 7)
    void weightsAtTheLimit() {
        assertEquals(0, LastStone.lastWeight(new int[] {1_000_000_000, 999_999_999, 1}));
        assertEquals(999_999_997, LastStone.lastWeight(new int[] {1_000_000_000, 1, 1, 1}));
    }

    @Test(order = 8)
    void inputIsAnyOrder() {
        assertEquals(2, LastStone.lastWeight(new int[] {4, 9, 3, 6}));
        assertEquals(2, LastStone.lastWeight(new int[] {3, 4, 6, 9}));
    }

    @Test(order = 9, timeoutMs = 3000)
    void twoHundredThousandStones() {
        int n = 200_000;
        int[] stones = new int[n];
        for (int i = 0; i < n; i++) {
            stones[i] = i + 1;
        }
        // pairs (200000, 199999) ... (4, 3) leave 99,999 ones; 2 and 1 leave a 1; 100,000 ones cancel
        assertEquals(0, LastStone.lastWeight(shuffled(stones, 3)));

        int[] scaled = new int[n - 1];
        for (int i = 0; i < n - 1; i++) {
            scaled[i] = 1000 * (i + 2); // 2,000 to 200,000,000
        }
        // the same pairing leaves 99,999 stones of 1,000 and one of 2,000, then an odd 99,999 of 1,000
        assertEquals(1000, LastStone.lastWeight(shuffled(scaled, 4)));
    }
}
