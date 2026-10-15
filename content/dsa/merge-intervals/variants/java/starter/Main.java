import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        int[][] ranges = {{14, 17}, {2, 5}, {4, 8}};
        System.out.println(Arrays.deepToString(Schedule.mergeRanges(ranges)));
    }
}
