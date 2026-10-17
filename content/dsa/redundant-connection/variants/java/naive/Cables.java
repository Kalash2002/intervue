import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/** Finds the cable that can be removed from a campus network so that it becomes a tree. */
public class Cables {

    /** Returns the spare cable: the last listed cable whose removal leaves every building connected. */
    static int[] spare(int[][] cables) {
        int n = cables.length;
        List<List<Integer>> touching = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            touching.add(new ArrayList<>());
        }
        for (int i = 0; i < n; i++) {
            touching.get(cables[i][0]).add(i);
            touching.get(cables[i][1]).add(i);
        }
        for (int skip = n - 1; skip >= 0; skip--) {
            if (reachedWithout(cables, touching, skip) == n) {
                return cables[skip];
            }
        }
        return new int[0];
    }

    private static int reachedWithout(int[][] cables, List<List<Integer>> touching, int skip) {
        boolean[] seen = new boolean[cables.length];
        Deque<Integer> queue = new ArrayDeque<>();
        seen[0] = true;
        queue.add(0);
        int reached = 1;
        while (!queue.isEmpty()) {
            int building = queue.poll();
            for (int c : touching.get(building)) {
                int other = cables[c][0] == building ? cables[c][1] : cables[c][0];
                if (c != skip && !seen[other]) {
                    seen[other] = true;
                    reached++;
                    queue.add(other);
                }
            }
        }
        return reached;
    }
}
