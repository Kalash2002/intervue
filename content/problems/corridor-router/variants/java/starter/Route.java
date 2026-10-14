import java.util.List;

/**
 * A path through the network.
 *
 * @param stops every node visited, starting node first; a route from a node to itself has one stop
 * @param totalFeeMinor sum of the fees of the corridors used
 */
public record Route(List<String> stops, long totalFeeMinor) {
    public Route {
        stops = List.copyOf(stops);
    }

    public int hops() {
        return stops.size() - 1;
    }
}
