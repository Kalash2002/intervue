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

/**
 * Naive solution: correct, and slow on purpose. fewestHops carries the phase 1 fix. cheapest enumerates
 * every simple path of at most maxHops corridors and keeps the best, which is exponential in maxHops.
 * Phases 1 and 2 pass; phase 3 must time out.
 */
public class RouteFinder {
    private final Network network;

    public RouteFinder(Network network) {
        this.network = network;
    }

    public Optional<Route> fewestHops(String from, String to) {
        if (!network.contains(from) || !network.contains(to)) {
            return Optional.empty();
        }
        Deque<String> queue = new ArrayDeque<>();
        Set<String> seen = new HashSet<>();
        Map<String, Corridor> arrivedBy = new HashMap<>();
        queue.add(from);
        seen.add(from);
        while (!queue.isEmpty()) {
            String current = queue.poll();
            if (current.equals(to)) {
                return Optional.of(buildRoute(from, to, arrivedBy));
            }
            for (Corridor corridor : network.corridorsFrom(current)) {
                if (corridor.active() && seen.add(corridor.to())) {
                    arrivedBy.put(corridor.to(), corridor);
                    queue.add(corridor.to());
                }
            }
        }
        return Optional.empty();
    }

    public Optional<Route> cheapest(String from, String to, int maxHops) {
        if (maxHops < 0) {
            throw new IllegalArgumentException("maxHops cannot be negative: " + maxHops);
        }
        if (!network.contains(from) || !network.contains(to)) {
            return Optional.empty();
        }
        if (from.equals(to)) {
            return Optional.of(new Route(List.of(from), 0));
        }
        Search search = new Search(to, maxHops);
        search.path.add(from);
        search.onPath.add(from);
        search.explore(from, 0);
        return Optional.ofNullable(search.best);
    }

    /** Depth-first enumeration of simple paths. Fees are never negative, so a simple path is always enough. */
    private final class Search {
        final String target;
        final int maxHops;
        final List<String> path = new ArrayList<>();
        final Set<String> onPath = new HashSet<>();
        Route best;

        Search(String target, int maxHops) {
            this.target = target;
            this.maxHops = maxHops;
        }

        void explore(String node, long feeSoFar) {
            if (path.size() - 1 >= maxHops) {
                return;
            }
            for (Corridor corridor : network.corridorsFrom(node)) {
                String next = corridor.to();
                if (!corridor.active() || onPath.contains(next)) {
                    continue;
                }
                path.add(next);
                onPath.add(next);
                long fee = feeSoFar + corridor.feeMinor();
                if (next.equals(target)) {
                    int hops = path.size() - 1;
                    if (best == null || fee < best.totalFeeMinor() || (fee == best.totalFeeMinor() && hops < best.hops())) {
                        best = new Route(path, fee);
                    }
                } else {
                    explore(next, fee);
                }
                path.remove(path.size() - 1);
                onPath.remove(next);
            }
        }
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
