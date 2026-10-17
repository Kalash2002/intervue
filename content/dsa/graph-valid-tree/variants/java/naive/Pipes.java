import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/** Decides whether a set of pipes joins valves into a single tree. */
public class Pipes {

    /** Returns true if the pipes connect all n valves with no loop. */
    static boolean isTree(int n, int[][] links) {
        List<List<Integer>> next = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            next.add(new ArrayList<>());
        }
        for (int[] link : links) {
            if (reachable(next, link[0])[link[1]]) {
                return false;
            }
            next.get(link[0]).add(link[1]);
            next.get(link[1]).add(link[0]);
        }
        for (boolean seen : reachable(next, 0)) {
            if (!seen) {
                return false;
            }
        }
        return true;
    }

    private static boolean[] reachable(List<List<Integer>> next, int from) {
        boolean[] seen = new boolean[next.size()];
        Deque<Integer> queue = new ArrayDeque<>();
        seen[from] = true;
        queue.add(from);
        while (!queue.isEmpty()) {
            for (int other : next.get(queue.poll())) {
                if (!seen[other]) {
                    seen[other] = true;
                    queue.add(other);
                }
            }
        }
        return seen;
    }
}
