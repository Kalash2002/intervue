public class Main {
    public static void main(String[] args) {
        int[][] prerequisites = {{3, 4}, {0, 4}, {1, 3}, {1, 0}, {2, 1}};
        System.out.println(Curriculum.canFinish(5, prerequisites));
    }
}
