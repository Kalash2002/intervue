public class Main {
    public static void main(String[] args) {
        int[][] prerequisites = {{1, 0}, {2, 1}, {3, 1}, {3, 2}};
        System.out.println(Curriculum.canFinish(4, prerequisites));
    }
}
