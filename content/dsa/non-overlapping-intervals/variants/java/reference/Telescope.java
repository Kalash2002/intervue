import java.util.Arrays;

/** Plans the requests a telescope serves, one at a time. */
public class Telescope {

    /** Returns the fewest requests to cancel so that no two of the rest overlap; each is [start, end). */
    static int minCancellations(int[][] requests) {
        int[][] byEnd = requests.clone();
        Arrays.sort(byEnd, (a, b) -> Integer.compare(a[1], b[1]));
        int cancelled = 0;
        int freeFrom = Integer.MIN_VALUE; // the end of the last request kept
        for (int[] request : byEnd) {
            if (request[0] >= freeFrom) { // touching is fine: the telescope is free at minute end
                freeFrom = request[1];
            } else {
                cancelled++; // overlaps the kept request, which ends no later than this one
            }
        }
        return cancelled;
    }
}
