import java.util.Arrays;

/** Counts the groups of cars that reach a gate when overtaking is not possible. */
public class Convoy {

    /** Returns the number of groups of cars that arrive at the gate. */
    static int groups(int gate, int[] start, int[] speed) {
        int n = start.length;
        long[] cars = new long[n];
        for (int i = 0; i < n; i++) {
            cars[i] = ((long) start[i] << 20) | speed[i]; // sorts by start position
        }
        Arrays.sort(cars);
        int groups = 0;
        double slowest = 0; // arrival time of the group at the front
        for (int i = n - 1; i >= 0; i--) { // from the car nearest the gate backwards
            int position = (int) (cars[i] >> 20);
            int carSpeed = (int) (cars[i] & 0xFFFFF);
            double time = (double) (gate - position) / carSpeed;
            if (time > slowest) { // arrives later than the group ahead: a new group
                groups++;
                slowest = time;
            }
        }
        return groups;
    }
}
