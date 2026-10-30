/** Finds the maximum of every window of k consecutive elements. */
public class WindowMax {

    /** Returns an array of nums.length - k + 1 maxima, one per window position. */
    static int[] maxima(int[] nums, int k) {
        int[] result = new int[nums.length - k + 1];
        for (int start = 0; start < result.length; start++) {
            int best = nums[start];
            for (int i = start + 1; i < start + k; i++) { // rescan the whole window
                if (nums[i] > best) {
                    best = nums[i];
                }
            }
            result[start] = best;
        }
        return result;
    }
}
