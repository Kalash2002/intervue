import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

/** Finds shortest word ladders. */
public class Ladder {

    /**
     * Returns the fewest single-letter changes from begin to end where every changed word is in
     * allowed, or -1 when there is no way. Returns 0 when begin equals end.
     */
    static int fewestChanges(String begin, String end, List<String> allowed) {
        if (begin.equals(end)) {
            return 0;
        }
        int n = allowed.size();
        boolean[] used = new boolean[n];
        Deque<String> queue = new ArrayDeque<>();
        Deque<Integer> depth = new ArrayDeque<>();
        queue.add(begin);
        depth.add(0);
        while (!queue.isEmpty()) {
            String word = queue.poll();
            int next = depth.poll() + 1;
            for (int j = 0; j < n; j++) { // compare with every allowed word
                if (!used[j] && oneApart(word, allowed.get(j))) {
                    used[j] = true;
                    if (allowed.get(j).equals(end)) {
                        return next;
                    }
                    queue.add(allowed.get(j));
                    depth.add(next);
                }
            }
        }
        return -1;
    }

    private static boolean oneApart(String a, String b) {
        int differences = 0;
        for (int i = 0; i < a.length(); i++) {
            if (a.charAt(i) != b.charAt(i)) {
                differences++;
            }
        }
        return differences == 1;
    }
}
