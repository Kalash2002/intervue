public class Main {
    public static void main(String[] args) {
        int[][] prerequisites = {{2, 0}, {2, 1}, {4, 2}, {3, 1}};
        System.out.println(java.util.Arrays.toString(StudyPlan.findOrder(5, prerequisites)));
    }
}
