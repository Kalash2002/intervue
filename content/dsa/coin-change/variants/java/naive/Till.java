/** Pays exact amounts from a till with unlimited coins of each value. */
public class Till {

    /** Returns the fewest coins adding up to exactly amount, or -1 if no combination does. */
    static int fewestCoins(int[] coins, int amount) {
        if (amount == 0) {
            return 0;
        }
        int best = -1;
        for (int coin : coins) {
            if (coin <= amount) { // hand over this coin first, then pay the rest as well as possible
                int rest = fewestCoins(coins, amount - coin);
                if (rest >= 0 && (best < 0 || rest + 1 < best)) {
                    best = rest + 1;
                }
            }
        }
        return best;
    }
}
