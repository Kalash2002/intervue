/** Finds the run of consecutive trading days with the largest total. */
public class Ledger {

    /** Returns the largest total of one or more consecutive days. */
    static int bestRun(int[] net) {
        int best = net[0];
        for (int start = 0; start < net.length; start++) {
            int total = 0;
            for (int end = start; end < net.length; end++) {
                total += net[end]; // the run start..end, built from the run start..end-1
                best = Math.max(best, total);
            }
        }
        return best;
    }
}
