public class Main {
    public static void main(String[] args) {
        int[][] links = {{0, 1}, {1, 2}, {3, 4}};
        System.out.println(Routes.connected(5, links, 0, 2));
    }
}
