import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Orders a stack of one-way tickets into a single trip that uses each ticket once. */
public class Itinerary {

    /** Returns the alphabetically smallest list of airports that uses every ticket once from start. */
    static List<String> route(String[][] tickets, String start) {
        int m = tickets.length;
        Map<String, List<Integer>> out = new HashMap<>();
        for (int i = 0; i < m; i++) {
            out.computeIfAbsent(tickets[i][0], k -> new ArrayList<>()).add(i);
        }
        for (List<Integer> options : out.values()) {
            options.sort((a, b) -> tickets[a][1].compareTo(tickets[b][1]));
        }
        boolean[] used = new boolean[m];
        int[] chosen = new int[m];
        int[] tried = new int[m + 1];
        int depth = 0;
        while (depth < m) {
            String here = depth == 0 ? start : tickets[chosen[depth - 1]][1];
            List<Integer> options = out.getOrDefault(here, List.of());
            int pos = tried[depth];
            while (pos < options.size() && used[options.get(pos)]) {
                pos++;
            }
            if (pos < options.size()) {
                int ticket = options.get(pos);
                used[ticket] = true;
                chosen[depth] = ticket;
                tried[depth] = pos + 1;
                depth++;
                tried[depth] = 0;
            } else {
                depth--;
                used[chosen[depth]] = false;
            }
        }
        List<String> route = new ArrayList<>();
        route.add(start);
        for (int ticket : chosen) {
            route.add(tickets[ticket][1]);
        }
        return route;
    }
}
