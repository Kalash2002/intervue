/** The smallest value of a sorted array that was rotated. */
public class RotatedMinimum {

    static int min(int[] nums) {
        int low = 0;
        int high = nums.length - 1; // the minimum always lies in [low, high]
        while (low < high) {
            int middle = (low + high) >>> 1;
            if (nums[middle] > nums[high]) {
                low = middle + 1; // the drop to the minimum is after the middle
            } else {
                high = middle; // the middle may itself be the minimum
            }
        }
        return nums[low];
    }
}
