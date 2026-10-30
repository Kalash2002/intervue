import static kit.Check.assertEquals;

import java.util.Arrays;
import java.util.Random;
import kit.Test;

public class Phase1Tests {

    private static void expect(int want, int[] coins, int amount) {
        String shown = coins.length <= 20 ? Arrays.toString(coins) : coins.length + " values";
        assertEquals(want, Purse.ways(coins.clone(), amount), "coins = " + shown + ", amount = " + amount);
    }

    @Test(order = 1)
    void sample() {
        expect(3, new int[] {3, 5, 8}, 16);
        expect(2, new int[] {4, 9, 7}, 18);
        expect(1, new int[] {4, 9}, 0);
    }

    @Test(order = 2)
    void theOrderOfTheCoinsDoesNotMatter() {
        // 2 x 6, and 5 + 5 + 2; counting orders would give 4
        expect(2, new int[] {2, 5}, 12);
        expect(2, new int[] {5, 2}, 12);
    }

    @Test(order = 3)
    void aValueCanBeUsedManyTimes() {
        // nine 4s, or four 9s; with each value used at most once nothing adds up to 36
        expect(2, new int[] {9, 4}, 36);
    }

    @Test(order = 4)
    void noHandfulAddsUp() {
        expect(0, new int[] {6, 4, 10}, 15);
    }

    @Test(order = 5)
    void coinsLargerThanTheAmountAreNeverUsed() {
        expect(1, new int[] {7, 20, 2}, 11);
        expect(1, new int[] {30, 4999, 5000}, 60);
        expect(1, new int[] {4999, 5000}, 0);
    }

    @Test(order = 6)
    void randomSmallTills() {
        Random random = new Random(518);
        for (int round = 0; round < 300; round++) {
            int[] coins = random.ints(1, 13).distinct().limit(2 + random.nextInt(3)).toArray();
            int amount = random.nextInt(31);
            expect(everyCount(coins, 0, amount), coins, amount);
        }
    }

    /** Tries every number of coins of each value in turn and counts the choices that add up to rest. */
    private static int everyCount(int[] coins, int index, int rest) {
        if (index == coins.length) {
            return rest == 0 ? 1 : 0;
        }
        int ways = 0;
        for (int count = 0; count * coins[index] <= rest; count++) {
            ways += everyCount(coins, index + 1, rest - count * coins[index]);
        }
        return ways;
    }

    @Test(order = 7)
    void threeHundredCoinValues() {
        // values 2001 to 2300: three coins are more than 4500, no single value is 4500,
        // and the pairs are 2200 + 2300 up to 2250 + 2250
        int[] coins = new int[300];
        for (int i = 0; i < coins.length; i++) {
            coins[i] = 2001 + i;
        }
        expect(51, coins, 4500);
    }

    @Test(order = 8, timeoutMs = 200)
    void evenCoinsCannotPayAnOddAmount() {
        // the counts for the even totals below 4999 run far past 2^63, but the answer is 0
        int[] coins = new int[20];
        for (int i = 0; i < coins.length; i++) {
            coins[i] = 40 - 2 * i;
        }
        expect(0, coins, 4999);
    }

    @Test(order = 9, timeoutMs = 200)
    void aLargeAmountWithSmallCoins() {
        // the sum over the number of 7s and of 4s of (what is left) / 3 + 1
        expect(249_133_433, new int[] {1, 3, 4, 7}, 5000);
    }

    @Test(order = 10, timeoutMs = 200)
    void theSameLargeAmountWithTheValuesReversed() {
        expect(249_133_433, new int[] {7, 4, 3, 1}, 5000);
    }
}
