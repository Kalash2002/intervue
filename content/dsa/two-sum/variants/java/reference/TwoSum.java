import java.util.HashMap;
import java.util.Map;

/** Finds the two positions whose values add up to the target. */
public class TwoSum {

    /** Returns {i, j} with i < j and nums[i] + nums[j] == target. */
    static int[] indices(int[] nums, int target) {
        Map<Integer, Integer> seen = new HashMap<>(); // value -> position, for values before this one
        for (int i = 0; i < nums.length; i++) {
            Integer partner = seen.get(target - nums[i]);
            if (partner != null) {
                return new int[] {partner, i}; // the earlier position is the smaller one
            }
            seen.put(nums[i], i); // stored after the lookup, so a value never pairs with itself
        }
        return new int[0];
    }
}
