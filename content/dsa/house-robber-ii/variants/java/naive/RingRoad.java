/** Plans which houses around a circular road to empty. */
public class RingRoad {

    /**
     * Returns the largest total of cash from houses no two of which are neighbours, where house
     * cash.length - 1 and house 0 are neighbours.
     */
    static int maxLoot(int[] cash) {
        int n = cash.length;
        if (n == 1) {
            return cash[0];
        }
        return Math.max(best(cash, 0, n - 2), best(cash, 1, n - 1)); // without the last, without the first
    }

    /** Best total from house i to house last, trying both choices for every house. */
    private static int best(int[] cash, int i, int last) {
        if (i > last) {
            return 0;
        }
        return Math.max(best(cash, i + 1, last), cash[i] + best(cash, i + 2, last));
    }
}
