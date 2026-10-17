public class Main {
    public static void main(String[] args) {
        GraphNode start = GraphNode.of(new int[] {5, 8, 3, 8}, new int[][] {{1, 3}, {0, 2}, {1, 3}, {2, 0}});
        GraphNode copy = CloneGraph.copy(start);
        String problem = GraphNode.deepCopyProblem(start, copy);
        System.out.println(problem == null ? "deep copy ok" : problem);
    }
}
