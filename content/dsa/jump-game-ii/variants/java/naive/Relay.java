/** Counts the fewest hops a message needs along a row of relay stations. */
public class Relay {

    /** Returns the fewest hops from station 0 to the last station. */
    static int fewestHops(int[] range) {
        int n = range.length;
        int[] hops = new int[n]; // hops[i]: fewest hops from station i to the last one
        for (int i = n - 2; i >= 0; i--) {
            int best = Integer.MAX_VALUE; // stays MAX_VALUE when no station in range gets there
            int farthest = Math.min(n - 1, i + range[i]);
            for (int j = i + 1; j <= farthest; j++) {
                best = Math.min(best, hops[j]);
            }
            hops[i] = best == Integer.MAX_VALUE ? best : best + 1;
        }
        return hops[0];
    }
}
