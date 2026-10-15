/** Finds the maximum of every window of k consecutive elements. */
public class WindowMax {

    /** Returns an array of nums.length - k + 1 maxima, one per window position. */
    static int[] maxima(int[] nums, int k) {
        int n = nums.length;
        int[] result = new int[n - k + 1];
        int[] deque = new int[n]; // positions; values decrease from front to back
        int front = 0;
        int back = 0; // the deque holds deque[front .. back - 1]
        for (int i = 0; i < n; i++) {
            while (back > front && nums[deque[back - 1]] <= nums[i]) {
                back--; // smaller or equal values before a new one can never win again
            }
            deque[back++] = i;
            if (deque[front] <= i - k) {
                front++; // the front left the window
            }
            if (i >= k - 1) {
                result[i - k + 1] = nums[deque[front]]; // the front is the window maximum
            }
        }
        return result;
    }
}
