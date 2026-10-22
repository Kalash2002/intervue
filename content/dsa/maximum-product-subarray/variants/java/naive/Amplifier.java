/** Finds the run of consecutive amplifier stages with the largest total gain. */
public class Amplifier {

    /** Returns the largest product of one or more consecutive gains. */
    static long bestGain(int[] gains) {
        long best = gains[0];
        for (int start = 0; start < gains.length; start++) {
            long product = 1;
            for (int end = start; end < gains.length; end++) {
                product *= gains[end]; // the run start..end, built from the run start..end-1
                best = Math.max(best, product);
            }
        }
        return best;
    }
}
