/** Counts the ways to climb a staircase one or two steps at a time. */
public class Staircase {

    /** Returns the number of move sequences of one or two steps that end exactly on step n. */
    static long countWays(int n) {
        if (n <= 2) {
            return n; // one way to reach step 1, two ways to reach step 2
        }
        return countWays(n - 1) + countWays(n - 2);
    }
}
