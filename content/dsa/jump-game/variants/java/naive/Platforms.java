/** Decides whether the springs on a row of platforms reach the last platform. */
public class Platforms {

    /** Returns true if some sequence of jumps leads from platform 0 to the last platform. */
    static boolean canReachEnd(int[] spring) {
        int n = spring.length;
        boolean[] good = new boolean[n]; // good[i]: the last platform can be reached from i
        good[n - 1] = true;
        for (int i = n - 2; i >= 0; i--) {
            int farthest = Math.min(n - 1, i + spring[i]);
            for (int j = i + 1; j <= farthest; j++) {
                if (good[j]) { // one good landing platform is enough
                    good[i] = true;
                    break;
                }
            }
        }
        return good[0];
    }
}
