import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;

/** Times how a signal spreads over a directed network. */
public class Signals {

    /**
     * Returns when the last of the n nodes receives a signal that starts at source at time 0, or -1
     * when some node is never reached. Each link is {from, to, delay}.
     */
    static int timeToReachAll(int n, int[][] links, int source) {
        List<List<int[]>> out = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            out.add(new ArrayList<>());
        }
        for (int[] link : links) {
            out.get(link[0]).add(new int[] {link[1], link[2]});
        }
        long[] best = new long[n];
        Arrays.fill(best, Long.MAX_VALUE);
        best[source] = 0;
        PriorityQueue<long[]> queue = new PriorityQueue<>((a, b) -> Long.compare(a[0], b[0]));
        queue.add(new long[] {0, source}); // time first, then node
        int settled = 0;
        long last = 0;
        while (!queue.isEmpty()) {
            long[] entry = queue.poll();
            int node = (int) entry[1];
            if (entry[0] > best[node]) {
                continue; // stale entry: a faster route was found after this was queued
            }
            settled++;
            last = entry[0];
            for (int[] link : out.get(node)) {
                long arrival = entry[0] + link[1];
                if (arrival < best[link[0]]) {
                    best[link[0]] = arrival;
                    queue.add(new long[] {arrival, link[0]});
                }
            }
        }
        return settled == n ? (int) last : -1;
    }
}
