import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        int[][] zones = {{10, 18}, {14, 15}, {16, 30}, {25, 27}};
        System.out.println(Arrays.toString(Coverage.smallestZone(zones, new int[] {15, 26, 9, 17})));
    }
}
