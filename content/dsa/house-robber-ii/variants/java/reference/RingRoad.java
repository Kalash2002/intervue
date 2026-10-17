/** Plans which houses around a circular road to empty. */
public class RingRoad {

    /**
     * Returns the largest total of cash from houses no two of which are neighbours, where house
     * cash.length - 1 and house 0 are neighbours.
     */
    static int maxLoot(int[] cash) {
        int n = cash.length;
        if (n == 1) {
            return cash[0]; // no neighbour; both streets below would be empty
        }
        // at least one end is left alone, so drop the last house or drop the first
        return Math.max(street(cash, 0, n - 2), street(cash, 1, n - 1));
    }

    /** Best total from the straight street of houses from..to, both included. */
    private static int street(int[] cash, int from, int to) {
        int twoBack = 0;
        int oneBack = 0;
        for (int i = from; i <= to; i++) {
            int here = Math.max(oneBack, twoBack + cash[i]); // leave house i, or take it
            twoBack = oneBack;
            oneBack = here;
        }
        return oneBack;
    }
}
