import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        int[][] grid = {{7, 12, 5, 9, 3}, {4, 0, 8, 6, 11}, {2, 14, 10, 0, 1}, {13, 6, 15, 8, 5}};
        SensorGrid.clearFailed(grid);
        System.out.println(Arrays.deepToString(grid));
    }
}
