import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

/** A node of an undirected graph. Read-only: the tests build and check graphs with the helpers. */
public class GraphNode {
    int val;
    List<GraphNode> neighbors = new ArrayList<>();

    GraphNode(int val) {
        this.val = val;
    }

    /**
     * Builds a graph where node i holds values[i] and lists the nodes adjacency[i] names, in that
     * order; returns node 0, or null when there are no values.
     */
    static GraphNode of(int[] values, int[][] adjacency) {
        GraphNode[] nodes = new GraphNode[values.length];
        for (int i = 0; i < values.length; i++) {
            nodes[i] = new GraphNode(values[i]);
        }
        for (int i = 0; i < values.length; i++) {
            for (int j : adjacency[i]) {
                nodes[i].neighbors.add(nodes[j]);
            }
        }
        return values.length == 0 ? null : nodes[0];
    }

    /** Every node reachable from start, in breadth-first order. */
    static List<GraphNode> reachable(GraphNode start) {
        List<GraphNode> order = new ArrayList<>();
        Map<GraphNode, Boolean> seen = new IdentityHashMap<>();
        if (start != null) {
            order.add(start);
            seen.put(start, true);
        }
        for (int i = 0; i < order.size(); i++) {
            for (GraphNode next : order.get(i).neighbors) {
                if (seen.put(next, true) == null) {
                    order.add(next);
                }
            }
        }
        return order;
    }

    /**
     * Returns null when copy is a deep copy of original (same values, same neighbours in the same
     * order, and not one node shared with original), otherwise what is wrong.
     */
    static String deepCopyProblem(GraphNode original, GraphNode copy) {
        if (original == null || copy == null) {
            return original == copy ? null : "the copy of " + (original == null ? "no graph should be null" : "a graph is null");
        }
        Map<GraphNode, Boolean> originals = new IdentityHashMap<>();
        for (GraphNode n : reachable(original)) {
            originals.put(n, true);
        }
        Map<GraphNode, GraphNode> copyOf = new IdentityHashMap<>();
        Map<GraphNode, GraphNode> originalOf = new IdentityHashMap<>();
        ArrayDeque<GraphNode> queue = new ArrayDeque<>();
        copyOf.put(original, copy);
        originalOf.put(copy, original);
        queue.add(original);
        while (!queue.isEmpty()) {
            GraphNode a = queue.poll();
            GraphNode b = copyOf.get(a);
            if (originals.containsKey(b)) {
                return "a node of the copy is a node of the input; make new nodes";
            }
            if (a.val != b.val) {
                return "a copied node holds " + b.val + " where the input node holds " + a.val;
            }
            if (a.neighbors.size() != b.neighbors.size()) {
                return "a copied node has " + b.neighbors.size() + " neighbours, the input node has " + a.neighbors.size();
            }
            for (int i = 0; i < a.neighbors.size(); i++) {
                GraphNode na = a.neighbors.get(i);
                GraphNode nb = b.neighbors.get(i);
                GraphNode known = copyOf.get(na);
                if (known == null) {
                    if (nb == null || originalOf.containsKey(nb)) {
                        return "two input nodes share one copy, or a neighbour is missing; each node needs its own copy";
                    }
                    copyOf.put(na, nb);
                    originalOf.put(nb, na);
                    queue.add(na);
                } else if (known != nb) {
                    return "neighbour " + i + " of a copied node is not the copy of the input's neighbour " + i;
                }
            }
        }
        return null;
    }
}
