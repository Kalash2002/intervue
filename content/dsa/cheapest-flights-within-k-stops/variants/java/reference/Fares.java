import java.util.Arrays;

/** Prices trips over a network of one-way flights. */
public class Fares {

    /**
     * Returns the lowest price of a trip from "from" to "to" with at most maxStops stops, or -1 when
     * there is none. Each flight is {from, to, price}.
     */
    static int cheapest(int n, int[][] flights, int from, int to, int maxStops) {
        long[] best = new long[n]; // cheapest known price to each city using the flights allowed so far
        Arrays.fill(best, Long.MAX_VALUE);
        best[from] = 0;
        for (int round = 0; round <= maxStops; round++) { // one more flight allowed per round
            long[] next = best.clone(); // prices carry over: fewer flights are still allowed
            for (int[] flight : flights) {
                if (best[flight[0]] != Long.MAX_VALUE && best[flight[0]] + flight[2] < next[flight[1]]) {
                    next[flight[1]] = best[flight[0]] + flight[2]; // read only the previous round
                }
            }
            best = next;
        }
        return best[to] == Long.MAX_VALUE ? -1 : (int) best[to];
    }
}
