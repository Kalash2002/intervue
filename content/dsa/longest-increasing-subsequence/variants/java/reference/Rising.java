/** Picks the most values, in order, that strictly rise. */
public class Rising {

    /** Returns the length of the longest strictly increasing subsequence of nums. */
    static int longest(int[] nums) {
        // tails[k]: the smallest value that ends a rising subsequence of length k + 1 so far;
        // tails[0..size) is strictly increasing
        int[] tails = new int[nums.length];
        int size = 0;
        for (int x : nums) {
            int lo = 0;
            int hi = size;
            while (lo < hi) { // the first tail >= x: an equal tail cannot be extended by x
                int mid = (lo + hi) >>> 1;
                if (tails[mid] < x) {
                    lo = mid + 1;
                } else {
                    hi = mid;
                }
            }
            tails[lo] = x; // a subsequence of length lo + 1 now ends at x: lower than before, or new
            if (lo == size) {
                size++;
            }
        }
        return size;
    }
}
