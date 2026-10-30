/** Positions in a sorted array. */
public class SortedSlots {

    static int insertAt(int[] sorted, int target) {
        int low = 0;
        int high = sorted.length; // the answer always lies in [low, high]
        while (low < high) {
            int middle = (low + high) >>> 1;
            if (sorted[middle] < target) {
                low = middle + 1; // the boundary is after the middle
            } else {
                high = middle; // the middle may itself be the boundary
            }
        }
        return low;
    }
}
