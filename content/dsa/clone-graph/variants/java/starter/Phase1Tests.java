import static kit.Check.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import kit.Test;

public class Phase1Tests {

    /** Copies the graph, checks the copy, and checks that no input node lost its value or neighbours. */
    private static void expect(int[] values, int[][] adjacency) {
        GraphNode start = GraphNode.of(values, adjacency);
        List<GraphNode> nodes = GraphNode.reachable(start);
        List<List<GraphNode>> before = new ArrayList<>();
        for (GraphNode n : nodes) {
            before.add(new ArrayList<>(n.neighbors));
        }
        GraphNode copy = CloneGraph.copy(start);
        for (int i = 0; i < nodes.size(); i++) {
            GraphNode n = nodes.get(i);
            assertTrue(n.neighbors.size() == before.get(i).size(), "an input node's neighbours changed; leave the input as it was");
            for (int k = 0; k < n.neighbors.size(); k++) {
                assertTrue(n.neighbors.get(k) == before.get(i).get(k), "an input node's neighbours changed; leave the input as it was");
            }
        }
        String problem = GraphNode.deepCopyProblem(start, copy);
        assertTrue(problem == null, problem);
    }

    @Test(order = 1)
    void sample() {
        expect(new int[] {5, 8, 3, 8}, new int[][] {{1, 3}, {0, 2}, {1, 3}, {2, 0}});
    }

    @Test(order = 2)
    void singleNodeWithNoNeighbours() {
        expect(new int[] {7}, new int[][] {{}});
    }

    @Test(order = 3)
    void noGraph() {
        assertTrue(CloneGraph.copy(null) == null, "the copy of no graph is null");
    }

    @Test(order = 4)
    void everyNodeHoldsTheSameValue() {
        // matching nodes by value instead of by identity merges these nodes
        expect(new int[] {4, 4, 4}, new int[][] {{1}, {0, 2}, {1}});
        expect(new int[] {-2, -2, -2, -2}, new int[][] {{1, 2, 3}, {0}, {0}, {0}});
    }

    @Test(order = 5)
    void twoNodes() {
        expect(new int[] {0, 1000}, new int[][] {{1}, {0}});
    }

    @Test(order = 6)
    void everyPairConnected() {
        expect(new int[] {9, -1000, 6, 2, 6}, new int[][] {
            {1, 2, 3, 4}, {0, 2, 3, 4}, {0, 1, 3, 4}, {0, 1, 2, 4}, {0, 1, 2, 3}});
    }

    @Test(order = 7)
    void neighbourOrderIsKept() {
        expect(new int[] {1, 2, 3, 4, 5}, new int[][] {{4, 2, 1}, {3, 0}, {0}, {1, 4}, {3, 0}});
    }

    @Test(order = 8, timeoutMs = 10000)
    void longPath() {
        int n = 100_000;
        int[] values = new int[n];
        int[][] adjacency = new int[n][];
        for (int i = 0; i < n; i++) {
            values[i] = i % 2001 - 1000;
            adjacency[i] = i == 0 ? new int[] {1} : i == n - 1 ? new int[] {n - 2} : new int[] {i - 1, i + 1};
        }
        expect(values, adjacency);
    }

    @Test(order = 9, timeoutMs = 10000)
    void largeRandomGraph() {
        int n = 100_000;
        Random random = new Random(81);
        List<List<Integer>> lists = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            lists.add(new ArrayList<>());
        }
        for (int i = 1; i < n; i++) { // a random tree keeps the graph connected
            int parent = random.nextInt(i);
            lists.get(i).add(parent);
            lists.get(parent).add(i);
        }
        for (int e = 0; e < 100_000; e++) { // then 100,000 more connections, each pair at most once
            int a = random.nextInt(n);
            int b = random.nextInt(n);
            if (a != b && !lists.get(a).contains(b)) {
                lists.get(a).add(b);
                lists.get(b).add(a);
            }
        }
        int[] values = new int[n];
        int[][] adjacency = new int[n][];
        for (int i = 0; i < n; i++) {
            values[i] = random.nextInt(5); // few distinct values
            adjacency[i] = lists.get(i).stream().mapToInt(Integer::intValue).toArray();
        }
        expect(values, adjacency);
    }
}
