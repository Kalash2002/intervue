/** Counts the choices of signs that bring a row of numbers to a target. */
public class Signs {

    /** Returns how many choices of a plus or minus sign for each number make the signed numbers add up to target. */
    static long ways(int[] nums, int target) {
        int sum = 0;
        for (int x : nums) {
            sum += x;
        }
        // the plus group P and the minus group M satisfy P - M = target and P + M = sum
        if (Math.abs(target) > sum || (sum + target) % 2 != 0) {
            return 0; // out of reach, or the plus group would need half a unit
        }
        int plus = (sum + target) / 2;
        long[] groups = new long[plus + 1]; // groups[s]: groups of the numbers so far, by position, totalling s
        groups[0] = 1; // the empty group
        for (int x : nums) {
            // downwards, so groups[s - x] does not yet include x and each number joins at most once;
            // a 0 runs down to s = 0 and doubles every count, in the group or not
            for (int s = plus; s >= x; s--) {
                groups[s] += groups[s - x];
            }
        }
        return groups[plus];
    }
}
