public class Main {
    public static void main(String[] args) {
        int[][] links = {{0, 1, 3}, {1, 2, 4}, {0, 2, 9}, {2, 3, 1}};
        System.out.println(Signals.timeToReachAll(4, links, 0));
    }
}
