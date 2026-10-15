import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** The set of corridors currently known to the router. Corridors can be added at any time. */
public class Network {
    private final Map<String, List<Corridor>> outgoing = new LinkedHashMap<>();
    private int corridorCount;

    public void add(Corridor corridor) {
        outgoing.computeIfAbsent(corridor.from(), k -> new ArrayList<>()).add(corridor);
        outgoing.computeIfAbsent(corridor.to(), k -> new ArrayList<>());
        corridorCount++;
    }

    public void add(String from, String to, long feeMinor) {
        add(new Corridor(from, to, feeMinor, true));
    }

    /** Corridors leaving {@code node}, in the order they were added. Empty for an unknown node. */
    public List<Corridor> corridorsFrom(String node) {
        return Collections.unmodifiableList(outgoing.getOrDefault(node, List.of()));
    }

    public boolean contains(String node) {
        return outgoing.containsKey(node);
    }

    public Set<String> nodes() {
        return Collections.unmodifiableSet(outgoing.keySet());
    }

    public int corridorCount() {
        return corridorCount;
    }
}
