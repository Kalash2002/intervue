import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        int[][] heights = {{1, 3, 4}, {2, 5, 3}, {6, 2, 1}};
        for (int[] cell : WaterFlow.bothOceans(heights)) {
            System.out.print(Arrays.toString(cell) + " ");
        }
        System.out.println();
    }
}
