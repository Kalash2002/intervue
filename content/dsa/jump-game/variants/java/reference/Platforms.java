/** Decides whether the springs on a row of platforms reach the last platform. */
public class Platforms {

    /** Returns true if some sequence of jumps leads from platform 0 to the last platform. */
    static boolean canReachEnd(int[] spring) {
        int reach = 0; // farthest platform reachable so far; every platform up to it is reachable
        for (int i = 0; i <= reach; i++) {
            reach = Math.max(reach, i + spring[i]);
            if (reach >= spring.length - 1) {
                return true;
            }
        }
        return false; // the next platform is beyond the reach: stuck
    }
}
