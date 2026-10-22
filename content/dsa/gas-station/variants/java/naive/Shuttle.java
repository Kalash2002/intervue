/** Finds where a shuttle on a circular route can start and still get all the way round. */
public class Shuttle {

    /** Returns the lowest-numbered stop from which the loop can be completed, or -1. */
    static int startStop(int[] charge, int[] drain) {
        int n = charge.length;
        for (int start = 0; start < n; start++) { // lowest-numbered first
            int battery = 0;
            int legs = 0; // legs driven so far without running out
            while (legs < n) {
                int stop = (start + legs) % n;
                battery += charge[stop] - drain[stop];
                if (battery < 0) {
                    break; // ran out on the leg from this stop
                }
                legs++;
            }
            if (legs == n) {
                return start;
            }
        }
        return -1;
    }
}
