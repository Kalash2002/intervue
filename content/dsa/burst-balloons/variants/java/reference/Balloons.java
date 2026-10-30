/** Picks the order in which to burst a row of numbered balloons. */
public class Balloons {

    /** Returns the most coins any order of bursting every balloon in values pays. */
    static int mostCoins(int[] values) {
        int n = values.length;
        int[] v = new int[n + 2]; // the row with a 1 at each end for the missing neighbours
        v[0] = 1;
        v[n + 1] = 1;
        System.arraycopy(values, 0, v, 1, n);
        // best[lo][hi]: the most coins from the balloons strictly between lo and hi, while both stand
        int[][] best = new int[n + 2][n + 2];
        for (int gap = 2; gap <= n + 1; gap++) { // shorter stretches first: each reads only shorter ones
            for (int lo = 0; lo + gap <= n + 1; lo++) {
                int hi = lo + gap;
                int most = 0;
                for (int k = lo + 1; k < hi; k++) {
                    // k bursts last between lo and hi, so lo and hi are its neighbours then
                    most = Math.max(most, best[lo][k] + v[lo] * v[k] * v[hi] + best[k][hi]);
                }
                best[lo][hi] = most;
            }
        }
        return best[0][n + 1];
    }
}
