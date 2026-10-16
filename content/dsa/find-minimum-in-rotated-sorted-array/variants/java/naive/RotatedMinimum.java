/** The smallest value of a sorted array that was rotated. */
public class RotatedMinimum {

    static int min(int[] nums) {
        int best = nums[0];
        for (int value : nums) {
            if (value < best) {
                best = value;
            }
        }
        return best;
    }
}
