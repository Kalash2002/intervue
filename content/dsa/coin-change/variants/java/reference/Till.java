import java.util.Arrays;

/** Pays exact amounts from a till with unlimited coins of each value. */
public class Till {

    /** Returns the fewest coins adding up to exactly amount, or -1 if no combination does. */
    static int fewestCoins(int[] coins, int amount) {
        // more than any real answer, which uses at most amount coins of value 1 or more,
        // and small enough that adding 1 cannot overflow
        int unreachable = amount + 1;
        int[] fewest = new int[amount + 1];
        Arrays.fill(fewest, unreachable);
        fewest[0] = 0;
        for (int total = 1; total <= amount; total++) {
            for (int coin : coins) {
                // coin <= total keeps the index in range; coin is never added to anything
                if (coin <= total && fewest[total - coin] + 1 < fewest[total]) {
                    fewest[total] = fewest[total - coin] + 1;
                }
            }
        }
        return fewest[amount] == unreachable ? -1 : fewest[amount];
    }
}
