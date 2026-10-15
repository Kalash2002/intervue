import java.util.HashSet;
import java.util.Set;

/** Finds the longest run of consecutive whole numbers in an unsorted array. */
public class Streaks {

    /** Returns the length of the longest run of values that each go up by one. */
    static int longest(int[] nums) {
        Set<Integer> values = new HashSet<>();
        for (int x : nums) {
            values.add(x); // repeats collapse into one entry
        }
        int best = 0;
        for (int x : values) {
            if (values.contains(x - 1)) {
                continue; // not the start of a run: its run is counted from the start
            }
            int length = 1;
            while (values.contains(x + length)) {
                length++; // walk up from the start
            }
            best = Math.max(best, length);
        }
        return best;
    }
}
