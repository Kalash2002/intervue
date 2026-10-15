/** Finds the two positions whose values add up to the target. */
public class TwoSum {

    /** Returns {i, j} with i < j and nums[i] + nums[j] == target. */
    static int[] indices(int[] nums, int target) {
        for (int i = 0; i < nums.length; i++) {
            for (int j = i + 1; j < nums.length; j++) {
                if (nums[i] + nums[j] == target) {
                    return new int[] {i, j};
                }
            }
        }
        return new int[0];
    }
}
