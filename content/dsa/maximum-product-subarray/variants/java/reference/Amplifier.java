/** Finds the run of consecutive amplifier stages with the largest total gain. */
public class Amplifier {

    /** Returns the largest product of one or more consecutive gains. */
    static long bestGain(int[] gains) {
        long high = gains[0]; // largest product of a run that ends at the current stage
        long low = gains[0]; // smallest product of a run that ends at the current stage
        long best = gains[0];
        for (int i = 1; i < gains.length; i++) {
            long gain = gains[i];
            if (gain < 0) { // a negative gain turns the smallest product into the largest
                long swap = high;
                high = low;
                low = swap;
            }
            high = Math.max(gain, high * gain); // start a new run here, or extend the one before
            low = Math.min(gain, low * gain);
            best = Math.max(best, high);
        }
        return best;
    }
}
