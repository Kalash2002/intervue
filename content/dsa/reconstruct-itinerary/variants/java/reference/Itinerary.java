import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

/** Orders a stack of one-way tickets into a single trip that uses each ticket once. */
public class Itinerary {

    /** Returns the alphabetically smallest list of airports that uses every ticket once from start. */
    static List<String> route(String[][] tickets, String start) {
        Map<String, PriorityQueue<String>> out = new HashMap<>();
        for (String[] ticket : tickets) {
            out.computeIfAbsent(ticket[0], k -> new PriorityQueue<>()).add(ticket[1]);
        }
        Deque<String> stack = new ArrayDeque<>();
        List<String> route = new ArrayList<>();
        stack.push(start);
        while (!stack.isEmpty()) {
            PriorityQueue<String> next = out.get(stack.peek());
            if (next != null && !next.isEmpty()) {
                stack.push(next.poll());
            } else {
                route.add(stack.pop());
            }
        }
        Collections.reverse(route);
        return route;
    }
}
