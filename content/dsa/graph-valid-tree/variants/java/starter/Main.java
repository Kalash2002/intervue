public class Main {
    public static void main(String[] args) {
        int[][] links = {{3, 0}, {0, 2}, {2, 4}, {0, 1}};
        System.out.println(Pipes.isTree(5, links));
    }
}
