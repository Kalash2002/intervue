/** Checks ride requests against the seats of one shuttle. */
public class Shuttle {

    /** Returns true if no minute has more than seats riders aboard; each ride is {riders, boardAt, leaveAt}. */
    static boolean canCarry(int[][] rides, int seats) {
        for (int[] mine : rides) {
            int aboard = 0; // riders aboard at the minute this group boards
            for (int[] other : rides) {
                if (other[1] <= mine[1] && mine[1] < other[2]) {
                    aboard += other[0];
                }
            }
            if (aboard > seats) {
                return false;
            }
        }
        return true;
    }
}
