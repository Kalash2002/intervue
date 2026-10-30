import java.util.*;

/** Finds the one value of an array that appears once, when every other value appears twice. */
public class LoneValue {

    /** Returns the value that appears exactly once in nums. */
    static int find(int[] nums) {
        for (int value : nums) {
            int copies = 0;
            for (int other : nums) { // count every copy of value, wherever it is
                if (other == value) {
                    copies++;
                }
            }
            if (copies == 1) {
                return value;
            }
        }
        return 0; // unreachable when exactly one value appears once
    }
}
