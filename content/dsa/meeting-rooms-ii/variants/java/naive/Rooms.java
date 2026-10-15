/** Plans room use for bookings. */
public class Rooms {

    /** Returns the fewest rooms for the bookings; each is [start, end), so a booking may start when another ends. */
    static int minRooms(int[][] bookings) {
        int peak = 0;
        for (int[] mine : bookings) {
            int active = 0; // bookings in use at the minute this one starts
            for (int[] other : bookings) {
                if (other[0] <= mine[0] && mine[0] < other[1]) {
                    active++;
                }
            }
            peak = Math.max(peak, active);
        }
        return peak;
    }
}
