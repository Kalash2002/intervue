import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        int[][] points = {{3, 4}, {-1, 2}, {6, -1}, {0, -3}};
        System.out.println(Arrays.deepToString(ClosestPoints.kClosest(points, 2)));
    }
}
