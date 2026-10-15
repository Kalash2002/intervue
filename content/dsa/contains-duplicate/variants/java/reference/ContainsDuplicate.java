import java.util.HashSet;
import java.util.Set;

/** Says whether any value occurs more than once. */
public class ContainsDuplicate {

    /** Returns true when some value appears at two or more positions. */
    static boolean hasDuplicate(int[] nums) {
        Set<Integer> seen = new HashSet<>();
        for (int value : nums) {
            if (!seen.add(value)) { // add returns false when the value was already there
                return true;
            }
        }
        return false;
    }
}
