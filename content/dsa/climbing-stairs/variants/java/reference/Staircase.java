/** Counts the ways to climb a staircase one or two steps at a time. */
public class Staircase {

    /** Returns the number of move sequences of one or two steps that end exactly on step n. */
    static long countWays(int n) {
        long twoBelow = 1; // ways to stand on the floor: do nothing
        long oneBelow = 1; // ways to stand on step 1
        for (int step = 2; step <= n; step++) {
            long here = oneBelow + twoBelow; // last move was a single step or a double step
            twoBelow = oneBelow;
            oneBelow = here;
        }
        return oneBelow;
    }
}
