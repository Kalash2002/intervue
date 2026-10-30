import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
        // Words that share a "shape" (one position blanked) are exactly the one-change neighbours.
        Map<String, List<String>> byShape = new HashMap<>();
        for (String word : allowed) {
            for (int i = 0; i < word.length(); i++) {
                byShape.computeIfAbsent(shape(word, i), k -> new ArrayList<>()).add(word);
            }
        }
        Set<String> reached = new HashSet<>(); // claimed when queued
        Deque<String> queue = new ArrayDeque<>();
        Map<String, Integer> changes = new HashMap<>();
        reached.add(begin);
        changes.put(begin, 0);
        queue.add(begin);
        while (!queue.isEmpty()) {
            String word = queue.poll();
            int next = changes.get(word) + 1;
            for (int i = 0; i < word.length(); i++) {
                for (String neighbour : byShape.getOrDefault(shape(word, i), List.of())) {
                    if (reached.add(neighbour)) {
                        if (neighbour.equals(end)) {
                            return next;
                        }
                        changes.put(neighbour, next);
                        queue.add(neighbour);
                    }
                }
            }
        }
        return -1;
    }

    private static String shape(String word, int blank) {
        return word.substring(0, blank) + "*" + word.substring(blank + 1);
    }
}
