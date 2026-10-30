/** Lookups in a strictly increasing array. */
public class SortedLookup {

    static int indexOf(int[] nums, int target) {
        int low = 0;
        int high = nums.length - 1; // if the target is present, it lies in [low, high]
        while (low <= high) {
            int middle = (low + high) >>> 1;
            if (nums[middle] == target) {
                return middle;
            }
            if (nums[middle] < target) { // compare, never subtract: the difference can overflow
                low = middle + 1;
            } else {
                high = middle - 1;
            }
        }
        return -1;
    }
}
