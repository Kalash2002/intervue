/** Finds the cheapest way up a staircase whose steps charge fees. */
public class StairFees {

    /**
     * Returns the smallest total fee to go from the floor below step 0 to the landing above the last
     * step, climbing one or two steps per move and paying fee[i] for each step i stood on.
     */
    static int minCost(int[] fee) {
        // cost to arrive at positions i - 2 and i - 1, their own fees not yet paid; 0 and 1 are free to reach
        int twoBack = 0;
        int oneBack = 0;
        for (int i = 2; i <= fee.length; i++) { // position fee.length is the landing
            int here = Math.min(oneBack + fee[i - 1], twoBack + fee[i - 2]);
            twoBack = oneBack;
            oneBack = here;
        }
        return oneBack;
    }
}
