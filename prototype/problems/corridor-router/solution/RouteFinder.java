import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/** Reference solution. */
public class RouteFinder {
    private static final long INF = Long.MAX_VALUE / 4;

    private final Network network;

    // Indexed copy of the network, rebuilt when the network changes.
    private int builtAtCorridorCount = -1;
    private Map<String, Integer> indexOf;
    private String[] names;
    private int[] edgeFrom;
    private int[] edgeTo;
    private long[] edgeFee;

    // Table for the last (origin, maxHops) asked: cost[h][v] = cheapest fee to v using at most h hops.
    private String cachedOrigin;
    private int cachedMaxHops = -1;
    private long[][] cost;
    private int[][] viaEdge;

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
                // Mark on enqueue: the first corridor to reach a node is on a shortest route.
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
        rebuildIndexIfNetworkChanged();
        if (!from.equals(cachedOrigin) || maxHops != cachedMaxHops) {
            fillTable(indexOf.get(from), maxHops);
            cachedOrigin = from;
            cachedMaxHops = maxHops;
        }
        int target = indexOf.get(to);
        int levels = cost.length - 1;
        long best = cost[levels][target];
        if (best >= INF) {
            return Optional.empty();
        }
        // Fewest hops among equally cheap routes: the first level that already reaches the best fee.
        int level = 0;
        while (cost[level][target] != best) {
            level++;
        }
        List<String> stops = new ArrayList<>();
        int node = target;
        stops.add(names[node]);
        while (level > 0) {
            int edge = viaEdge[level][node];
            if (edge >= 0) {
                node = edgeFrom[edge];
                stops.add(names[node]);
            }
            level--;
        }
        Collections.reverse(stops);
        return Optional.of(new Route(stops, best));
    }

    private void rebuildIndexIfNetworkChanged() {
        if (builtAtCorridorCount == network.corridorCount()) {
            return;
        }
        indexOf = new HashMap<>();
        List<String> nodeList = new ArrayList<>(network.nodes());
        names = nodeList.toArray(new String[0]);
        for (int i = 0; i < names.length; i++) {
            indexOf.put(names[i], i);
        }
        List<Corridor> active = new ArrayList<>();
        for (String node : nodeList) {
            for (Corridor c : network.corridorsFrom(node)) {
                if (c.active()) {
                    active.add(c);
                }
            }
        }
        edgeFrom = new int[active.size()];
        edgeTo = new int[active.size()];
        edgeFee = new long[active.size()];
        for (int i = 0; i < active.size(); i++) {
            Corridor c = active.get(i);
            edgeFrom[i] = indexOf.get(c.from());
            edgeTo[i] = indexOf.get(c.to());
            edgeFee[i] = c.feeMinor();
        }
        builtAtCorridorCount = network.corridorCount();
        cachedOrigin = null;
        cachedMaxHops = -1;
    }

    /** Bellman-Ford limited to maxHops rounds; stops early once a round changes nothing. */
    private void fillTable(int origin, int maxHops) {
        int n = names.length;
        int rounds = Math.min(maxHops, Math.max(0, n - 1));
        List<long[]> costs = new ArrayList<>();
        List<int[]> vias = new ArrayList<>();
        long[] first = new long[n];
        Arrays.fill(first, INF);
        first[origin] = 0;
        costs.add(first);
        int[] noVia = new int[n];
        Arrays.fill(noVia, -1);
        vias.add(noVia);
        for (int round = 1; round <= rounds; round++) {
            long[] prev = costs.get(round - 1);
            long[] cur = prev.clone();
            int[] via = new int[n];
            Arrays.fill(via, -1);
            boolean changed = false;
            for (int e = 0; e < edgeFee.length; e++) {
                long base = prev[edgeFrom[e]];
                if (base < INF && base + edgeFee[e] < cur[edgeTo[e]]) {
                    cur[edgeTo[e]] = base + edgeFee[e];
                    via[edgeTo[e]] = e;
                    changed = true;
                }
            }
            if (!changed) {
                break;
            }
            costs.add(cur);
            vias.add(via);
        }
        cost = costs.toArray(new long[0][]);
        viaEdge = vias.toArray(new int[0][]);
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
