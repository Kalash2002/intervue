import java.util.Arrays;

/** Checks ride requests against the seats of one shuttle. */
public class Shuttle {

    /** Returns true if no minute has more than seats riders aboard; each ride is {riders, boardAt, leaveAt}. */
    static boolean canCarry(int[][] rides, int seats) {
        int[][] events = new int[rides.length * 2][];
        for (int i = 0; i < rides.length; i++) {
            events[2 * i] = new int[] {rides[i][1], rides[i][0]};
            events[2 * i + 1] = new int[] {rides[i][2], -rides[i][0]};
        }
        Arrays.sort(events, (a, b) -> a[0] != b[0] ? Integer.compare(a[0], b[0]) : Integer.compare(a[1], b[1]));
        int aboard = 0;
        for (int[] event : events) {
            aboard += event[1];
            if (aboard > seats) {
                return false;
            }
        }
        return true;
    }
}
