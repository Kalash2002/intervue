/** Plans which houses along a street to empty. */
public class Street {

    /** Returns the largest total of cash from houses no two of which are neighbours. */
    static int maxLoot(int[] cash) {
        return best(cash, 0);
    }

    /** Best total from house i to the end of the street. */
    private static int best(int[] cash, int i) {
        if (i >= cash.length) {
            return 0;
        }
        return Math.max(best(cash, i + 1), cash[i] + best(cash, i + 2)); // leave house i, or take it
    }
}
