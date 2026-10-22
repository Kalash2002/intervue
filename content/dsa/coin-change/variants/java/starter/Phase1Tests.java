import static kit.Check.assertEquals;

import java.util.Arrays;
import java.util.Random;
import kit.Test;

public class Phase1Tests {

    private static void expect(int want, int[] coins, int amount) {
        String input = "coins = " + Arrays.toString(coins) + ", amount = " + amount;
        assertEquals(want, Till.fewestCoins(coins.clone(), amount), input);
    }

    @Test(order = 1)
    void sample() {
        expect(2, new int[] {2, 7, 10}, 14);
        expect(3, new int[] {4, 7}, 15);
        expect(-1, new int[] {6, 9}, 20);
        expect(3, new int[] {2_147_483_647, 3}, 9);
    }

    @Test(order = 2)
    void anAmountOfZeroTakesNoCoins() {
        expect(0, new int[] {8, 3}, 0);
        expect(0, new int[] {5, 2_147_483_647}, 0);
    }

    @Test(order = 3)
    void theLargestCoinFirstIsNotFewest() {
        expect(2, new int[] {1, 6, 10}, 12); // 6 + 6; the 10 first needs 1 + 1 after it
        expect(3, new int[] {3, 11, 12}, 33); // three 11s; two 12s first need three 3s after them
    }

    @Test(order = 4)
    void theLargestCoinFirstCanFindNothing() {
        expect(3, new int[] {6, 10}, 22); // 10 + 6 + 6; two 10s first leave 2
        expect(5, new int[] {5, 8}, 31); // 8 + 8 + 5 + 5 + 5; three 8s and a 5 leave 2
    }

    @Test(order = 5)
    void noCombinationAddsUp() {
        expect(-1, new int[] {4, 10}, 7);
        expect(-1, new int[] {7, 11}, 16);
    }

    @Test(order = 6)
    void exactlyOneCoinOrOneValue() {
        expect(1, new int[] {9, 4}, 9);
        expect(1000, new int[] {5}, 5000);
    }

    @Test(order = 7)
    void coinsUpToTheLargestInt() {
        expect(3, new int[] {2_147_483_647, 5, 3}, 11);
        expect(3, new int[] {1, 2_147_483_647}, 3);
        expect(-1, new int[] {2_147_483_646, 2_147_483_647}, 5000);
    }

    @Test(order = 8)
    void randomSmallTills() {
        Random random = new Random(322);
        for (int round = 0; round < 300; round++) {
            int[] coins = random.ints(1, 16).distinct().limit(1 + random.nextInt(4)).toArray();
            int amount = random.nextInt(25);
            expect(everyCount(coins, 0, amount), coins, amount);
        }
    }

    /** Tries every number of coins of each value in turn; -1 if none adds up to rest. */
    private static int everyCount(int[] coins, int index, int rest) {
        if (index == coins.length) {
            return rest == 0 ? 0 : -1;
        }
        int best = -1;
        for (int count = 0; count * coins[index] <= rest; count++) {
            int others = everyCount(coins, index + 1, rest - count * coins[index]);
            if (others >= 0 && (best < 0 || count + others < best)) {
                best = count + others;
            }
        }
        return best;
    }

    @Test(order = 9, timeoutMs = 200)
    void aLargeAmountNoEvenCoinsCanPay() {
        expect(-1, new int[] {4, 6, 10, 14}, 4999);
    }

    @Test(order = 10, timeoutMs = 200)
    void aLargeAmountJustShortOfARoundNumber() {
        // 199 coins make at most 4975. With 200, 201 or 202 coins, all 25s would overshoot by 1, 26
        // or 51, and each 14 takes off 11 and each 9 takes off 16, which never adds up to those.
        // 197 x 25 + 4 x 14 + 2 x 9 = 4999 uses 203.
        expect(203, new int[] {9, 14, 25}, 4999);
    }

    @Test(order = 11, timeoutMs = 200)
    void aLargeAmountWithTwelveCoinValues() {
        // 51 x 97 = 4947 falls short, and 51 x 97 + 53 = 5000 uses 52
        expect(52, new int[] {97, 89, 83, 79, 73, 71, 67, 61, 59, 53, 47, 43}, 5000);
    }
}
