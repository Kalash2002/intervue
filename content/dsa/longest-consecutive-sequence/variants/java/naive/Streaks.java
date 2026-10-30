import java.util.HashSet;
import java.util.Set;

/** Finds the longest run of consecutive whole numbers in an unsorted array. */
public class Streaks {

    /** Returns the length of the longest run of values that each go up by one. */
    static int longest(int[] nums) {
        Set<Integer> values = new HashSet<>();
        for (int x : nums) {
            values.add(x);
        }
        int best = 0;
        for (int x : nums) {
            int length = 1;
            while (values.contains(x + length)) {
                length++;
            }
            best = Math.max(best, length);
        }
        return best;
    }
}
