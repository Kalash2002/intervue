public class Main {
    public static void main(String[] args) {
        GraphNode start = GraphNode.of(new int[] {6, 2, 6, 9, 2}, new int[][] {{2, 1}, {0, 2}, {3, 1, 0}, {4, 2}, {3}});
        GraphNode copy = CloneGraph.copy(start);
        String problem = GraphNode.deepCopyProblem(start, copy);
        System.out.println(problem == null ? "deep copy ok" : problem);
    }
}
