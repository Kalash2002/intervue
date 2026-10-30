import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;

/** Makes a deep copy of a connected undirected graph. */
public class CloneGraph {

    /** Returns the copy of start in a new graph with the same values and connections; null for null. */
    static GraphNode copy(GraphNode start) {
        if (start == null) {
            return null;
        }
        Map<GraphNode, GraphNode> copyOf = new HashMap<>(); // original -> its copy; GraphNode keys compare by identity
        ArrayDeque<GraphNode> queue = new ArrayDeque<>();
        copyOf.put(start, new GraphNode(start.val));
        queue.add(start);
        while (!queue.isEmpty()) {
            GraphNode node = queue.poll();
            GraphNode twin = copyOf.get(node);
            for (GraphNode next : node.neighbors) {
                GraphNode nextTwin = copyOf.get(next);
                if (nextTwin == null) { // first time we meet next: copy it now, wire it later
                    nextTwin = new GraphNode(next.val);
                    copyOf.put(next, nextTwin);
                    queue.add(next);
                }
                twin.neighbors.add(nextTwin); // same order as the original's list
            }
        }
        return copyOf.get(start);
    }
}
