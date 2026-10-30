import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        int[][] grid = {{8, 3, 15, 22, 6}, {11, 40, 2, 9, 17}, {5, 13, 0, 31, 24}, {19, 7, 28, 4, 12}, {33, 21, 10, 16, 1}};
        Board.rotate(grid);
        System.out.println(Arrays.deepToString(grid));
    }
}
