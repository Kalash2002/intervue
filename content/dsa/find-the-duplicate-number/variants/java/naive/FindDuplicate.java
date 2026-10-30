/** Finds the value that appears more than once in an array of n + 1 values from 1 to n. */
public class FindDuplicate {

    /** Returns the repeated value without changing nums. */
    static int find(int[] nums) {
        for (int i = 0; i < nums.length; i++) {
            for (int j = i + 1; j < nums.length; j++) {
                if (nums[i] == nums[j]) {
                    return nums[i];
                }
            }
        }
        throw new IllegalArgumentException("no value repeats");
    }
}
