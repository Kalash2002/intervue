import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/** Answers whether two stations of a tram network are joined by connectors. */
public class Routes {

    /** Returns true if station to can be reached from station from; connectors run both ways. */
    static boolean connected(int n, int[][] links, int from, int to) {
        List<List<Integer>> next = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            next.add(new ArrayList<>());
        }
        for (int[] link : links) {
            next.get(link[0]).add(link[1]);
            next.get(link[1]).add(link[0]);
        }
        boolean[] seen = new boolean[n];
        Deque<Integer> queue = new ArrayDeque<>();
        seen[from] = true;
        queue.add(from);
        while (!queue.isEmpty()) {
            int station = queue.poll();
            if (station == to) {
                return true;
            }
            for (int neighbour : next.get(station)) {
                if (!seen[neighbour]) {
                    seen[neighbour] = true;
                    queue.add(neighbour);
                }
            }
        }
        return false;
    }
}
