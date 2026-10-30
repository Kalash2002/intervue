import java.util.Arrays;

/** Checks bookings for a single meeting room. */
public class Rooms {

    /** Returns true if no two bookings clash; each is [start, end), so a booking may start when another ends. */
    static boolean fitInOneRoom(int[][] bookings) {
        int[][] byStart = bookings.clone();
        Arrays.sort(byStart, (a, b) -> Integer.compare(a[0], b[0]));
        for (int i = 1; i < byStart.length; i++) {
            if (byStart[i][0] < byStart[i - 1][1]) { // starts before the previous one ends
                return false;
            }
        }
        return true;
    }
}
