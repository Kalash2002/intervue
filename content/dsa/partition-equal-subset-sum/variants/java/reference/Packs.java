/** Shares a trip's gear between two packs of equal weight. */
public class Packs {

    /** Returns whether the items can be split into two groups of exactly the same weight. */
    static boolean canBalance(int[] weights) {
        int total = 0;
        for (int w : weights) {
            total += w;
        }
        if (total % 2 != 0) {
            return false; // an odd total has no half
        }
        int half = total / 2;
        boolean[] reachable = new boolean[half + 1]; // reachable[s]: some of the items so far weigh s
        reachable[0] = true;
        for (int w : weights) {
            // downwards, so reachable[s - w] still means "without this item" and it is used once
            for (int s = half; s >= w; s--) {
                if (reachable[s - w]) {
                    reachable[s] = true;
                }
            }
        }
        return reachable[half];
    }
}
