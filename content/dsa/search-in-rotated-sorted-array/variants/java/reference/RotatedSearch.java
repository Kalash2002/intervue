/** Lookups in a sorted array that was rotated. */
public class RotatedSearch {

    static int indexOf(int[] nums, int target) {
        int low = 0;
        int high = nums.length - 1; // if the target is present, it lies in [low, high]
        while (low <= high) {
            int middle = (low + high) >>> 1;
            if (nums[middle] == target) {
                return middle;
            }
            if (nums[low] <= nums[middle]) { // [low, middle] is sorted; <= because low may be middle
                if (nums[low] <= target && target < nums[middle]) {
                    high = middle - 1;
                } else {
                    low = middle + 1;
                }
            } else { // [middle, high] is sorted
                if (nums[middle] < target && target <= nums[high]) {
                    low = middle + 1;
                } else {
                    high = middle - 1;
                }
            }
        }
        return -1;
    }
}
