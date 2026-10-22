/** Finds the run of consecutive trading days with the largest total. */
public class Ledger {

    /** Returns the largest total of one or more consecutive days. */
    static int bestRun(int[] net) {
        int endingHere = net[0]; // best total of a run that ends on the current day
        int best = net[0];
        for (int i = 1; i < net.length; i++) {
            // a run before today with a negative total only lowers today's, so start afresh
            endingHere = Math.max(net[i], endingHere + net[i]);
            best = Math.max(best, endingHere);
        }
        return best;
    }
}
