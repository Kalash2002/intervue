/** Checks bookings for a single meeting room. */
public class Rooms {

    /** Returns true if no two bookings clash; each is [start, end), so a booking may start when another ends. */
    static boolean fitInOneRoom(int[][] bookings) {
        for (int i = 0; i < bookings.length; i++) {
            for (int j = i + 1; j < bookings.length; j++) {
                int[] a = bookings[i];
                int[] b = bookings[j];
                if (a[0] < b[1] && b[0] < a[1]) { // each starts before the other ends: a clash
                    return false;
                }
            }
        }
        return true;
    }
}
