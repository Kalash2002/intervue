/** Counts the ways a vending machine can pay out change. */
public class Purse {

    /** Returns the number of different handfuls of the coin values, any number of each, that add up to amount. */
    static int ways(int[] coins, int amount) {
        int[] ways = new int[amount + 1]; // ways[t]: handfuls of the values seen so far that make t
        ways[0] = 1; // the empty handful
        for (int coin : coins) { // values on the outside: each handful is built in one fixed order of values
            for (int t = coin; t <= amount; t++) {
                // upwards, so ways[t - coin] already counts handfuls with this value and it can repeat;
                // totals the answer never draws on may wrap, which leaves every entry right modulo 2^32
                ways[t] += ways[t - coin];
            }
        }
        return ways[amount];
    }
}
