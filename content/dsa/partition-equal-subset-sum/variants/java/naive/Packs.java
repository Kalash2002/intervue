/** Shares a trip's gear between two packs of equal weight. */
public class Packs {

    /** Returns whether the items can be split into two groups of exactly the same weight. */
    static boolean canBalance(int[] weights) {
        int total = 0;
        for (int w : weights) {
            total += w;
        }
        return total % 2 == 0 && fills(weights, 0, total / 2);
    }

    /** Whether some of the items from position i on weigh exactly need. */
    private static boolean fills(int[] weights, int i, int need) {
        if (need == 0) {
            return true;
        }
        if (i == weights.length || need < 0) {
            return false; // no items left, or this pack is already too heavy
        }
        // item i goes into the first pack, or it does not
        return fills(weights, i + 1, need - weights[i]) || fills(weights, i + 1, need);
    }
}
