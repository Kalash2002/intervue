import java.util.ArrayList;
import java.util.List;

/** Prices trips over a network of one-way flights. */
public class Fares {

    /**
     * Returns the lowest price of a trip from "from" to "to" with at most maxStops stops, or -1 when
     * there is none. Each flight is {from, to, price}.
     */
    static int cheapest(int n, int[][] flights, int from, int to, int maxStops) {
        List<List<int[]>> out = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            out.add(new ArrayList<>());
        }
        for (int[] flight : flights) {
            out.get(flight[0]).add(flight);
        }
        long best = explore(out, from, to, maxStops + 1, 0);
        return best == Long.MAX_VALUE ? -1 : (int) best;
    }

    /** Tries every flight out of city, using at most flightsLeft more flights. */
    private static long explore(List<List<int[]>> out, int city, int to, int flightsLeft, long spent) {
        if (city == to) {
            return spent;
        }
        if (flightsLeft == 0) {
            return Long.MAX_VALUE;
        }
        long best = Long.MAX_VALUE;
        for (int[] flight : out.get(city)) {
            best = Math.min(best, explore(out, flight[1], to, flightsLeft - 1, spent + flight[2]));
        }
        return best;
    }
}
