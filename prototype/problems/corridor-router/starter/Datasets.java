import java.util.Random;

/** Builds the large networks used by the phase 3 tests. Same seed, same network. */
public final class Datasets {
    private Datasets() {}

    public static String node(int index) {
        return "N" + index;
    }

    /**
     * A random network. Every node has a corridor to the next one so that most pairs are connected;
     * the remaining corridors join random pairs. Fees are between 100 and 10,000.
     */
    public static Network randomNetwork(int nodes, int corridors, long seed) {
        Random random = new Random(seed);
        Network network = new Network();
        for (int i = 0; i < nodes; i++) {
            network.add(node(i), node((i + 1) % nodes), 100 + random.nextInt(9_901));
        }
        for (int i = nodes; i < corridors; i++) {
            int from = random.nextInt(nodes);
            int to = random.nextInt(nodes);
            if (from == to) {
                to = (to + 1) % nodes;
            }
            boolean active = random.nextInt(20) != 0;
            network.add(new Corridor(node(from), node(to), 100 + random.nextInt(9_901), active));
        }
        return network;
    }
}
