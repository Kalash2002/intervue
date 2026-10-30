import java.util.Arrays;

/** Times how a signal spreads over a directed network. */
public class Signals {

    /**
     * Returns when the last of the n nodes receives a signal that starts at source at time 0, or -1
     * when some node is never reached. Each link is {from, to, delay}.
     */
    static int timeToReachAll(int n, int[][] links, int source) {
        long[] best = new long[n];
        Arrays.fill(best, Long.MAX_VALUE);
        best[source] = 0;
        boolean changed = true;
        while (changed) { // sweep over every link until nothing improves
            changed = false;
            for (int[] link : links) {
                if (best[link[0]] != Long.MAX_VALUE && best[link[0]] + link[2] < best[link[1]]) {
                    best[link[1]] = best[link[0]] + link[2];
                    changed = true;
                }
            }
        }
        long last = 0;
        for (long time : best) {
            if (time == Long.MAX_VALUE) {
                return -1;
            }
            last = Math.max(last, time);
        }
        return (int) last;
    }
}
