import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/** Finds routes for a payment through the corridor network. */
public class RouteFinder {
    private final Network network;

    public RouteFinder(Network network) {
        this.network = network;
    }

    /**
     * The route from {@code from} to {@code to} that uses the fewest corridors.
     * Inactive corridors are never used. Empty when no route exists.
     */
    public Optional<Route> fewestHops(String from, String to) {
        if (!network.contains(from) || !network.contains(to)) {
            return Optional.empty();
        }
        Deque<String> queue = new ArrayDeque<>();
        Set<String> visited = new HashSet<>();
        Map<String, Corridor> arrivedBy = new HashMap<>();
        queue.add(from);
        while (!queue.isEmpty()) {
            String current = queue.poll();
            if (!visited.add(current)) {
                continue;
            }
            if (current.equals(to)) {
                return Optional.of(buildRoute(from, to, arrivedBy));
            }
            for (Corridor corridor : network.corridorsFrom(current)) {
                if (!corridor.active() || visited.contains(corridor.to())) {
                    continue;
                }
                arrivedBy.put(corridor.to(), corridor);
                queue.add(corridor.to());
            }
        }
        return Optional.empty();
    }

    /**
     * The route from {@code from} to {@code to} with the lowest total fee that uses at most
     * {@code maxHops} corridors. See the brief for the full rules.
     */
    public Optional<Route> cheapest(String from, String to, int maxHops) {
        throw new UnsupportedOperationException("cheapest is not implemented yet");
    }

    private Route buildRoute(String from, String to, Map<String, Corridor> arrivedBy) {
        List<String> stops = new ArrayList<>();
        long fee = 0;
        String node = to;
        stops.add(node);
        while (!node.equals(from)) {
            Corridor used = arrivedBy.get(node);
            fee += used.feeMinor();
            node = used.from();
            stops.add(node);
        }
        Collections.reverse(stops);
        return new Route(stops, fee);
    }
}
