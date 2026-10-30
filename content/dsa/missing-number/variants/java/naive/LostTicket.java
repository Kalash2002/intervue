import java.util.*;

/** Finds the one ticket number of 0 to n that is missing from the n numbers left. */
public class LostTicket {

    /** Returns the number of 0 to nums.length that is not in nums. */
    static int find(int[] nums) {
        int n = nums.length;
        for (int candidate = 0; candidate <= n; candidate++) { // the range has n + 1 numbers
            boolean seen = false;
            for (int value : nums) {
                if (value == candidate) {
                    seen = true;
                    break;
                }
            }
            if (!seen) {
                return candidate;
            }
        }
        return -1; // unreachable: n distinct values cannot cover n + 1 numbers
    }
}
