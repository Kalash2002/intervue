import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        int[][] booked = {{9, 12}, {14, 16}, {18, 20}};
        System.out.println(Arrays.deepToString(Bookings.insert(booked, new int[] {11, 15})));
    }
}
