import java.util.Arrays;

/** Plans room use for bookings. */
public class Rooms {

    /** Returns the fewest rooms for the bookings; each is [start, end), so a booking may start when another ends. */
    static int minRooms(int[][] bookings) {
        int n = bookings.length;
        int[] starts = new int[n];
        int[] ends = new int[n];
        for (int i = 0; i < n; i++) {
            starts[i] = bookings[i][0];
            ends[i] = bookings[i][1];
        }
        Arrays.sort(starts);
        Arrays.sort(ends);
        int busy = 0;
        int peak = 0;
        int released = 0; // how many bookings have already ended
        for (int start : starts) {
            while (ends[released] <= start) { // an end at this minute frees a room first
                released++;
                busy--;
            }
            busy++;
            peak = Math.max(peak, busy);
        }
        return peak;
    }
}
