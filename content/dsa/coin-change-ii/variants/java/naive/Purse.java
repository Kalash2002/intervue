/** Counts the ways a vending machine can pay out change. */
public class Purse {

    /** Returns the number of different handfuls of the coin values, any number of each, that add up to amount. */
    static int ways(int[] coins, int amount) {
        return from(coins, 0, amount);
    }

    /** Ways to pay rest with the coin values from position i on. */
    private static int from(int[] coins, int i, int rest) {
        if (rest == 0) {
            return 1; // the handful is complete
        }
        if (i == coins.length) {
            return 0; // every value decided, and something is still owed
        }
        int ways = from(coins, i + 1, rest); // no more coins of value i
        if (coins[i] <= rest) {
            ways += from(coins, i, rest - coins[i]); // one more coin of value i, which may be used again
        }
        return ways;
    }
}
