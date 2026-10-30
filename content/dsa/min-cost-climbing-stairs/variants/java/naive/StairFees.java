/** Finds the cheapest way up a staircase whose steps charge fees. */
public class StairFees {

    /**
     * Returns the smallest total fee to go from the floor below step 0 to the landing above the last
     * step, climbing one or two steps per move and paying fee[i] for each step i stood on.
     */
    static int minCost(int[] fee) {
        return Math.min(from(fee, 0), from(fee, 1)); // the first move lands on step 0 or step 1
    }

    /** Cheapest total from landing on the given step to the top, that step's fee included. */
    private static int from(int[] fee, int step) {
        if (step >= fee.length) {
            return 0; // reached the landing
        }
        return fee[step] + Math.min(from(fee, step + 1), from(fee, step + 2));
    }
}
