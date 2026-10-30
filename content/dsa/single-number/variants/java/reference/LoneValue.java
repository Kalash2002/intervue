import java.util.*;

/** Finds the one value of an array that appears once, when every other value appears twice. */
public class LoneValue {

    /** Returns the value that appears exactly once in nums. */
    static int find(int[] nums) {
        int lone = 0;
        for (int value : nums) {
            lone ^= value; // the two copies of a value cancel, in any order, and 0 changes nothing
        }
        return lone;
    }
}
