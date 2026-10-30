import java.util.Random;

/** Finds an order statistic of an array without sorting it. */
public class KthElement {

    /** Returns the k-th largest value in nums, counting repeats; nums may be reordered. */
    static int kthLargest(int[] nums, int k) {
        int target = nums.length - k; // its position if nums were sorted ascending
        Random random = new Random();
        int lo = 0;
        int hi = nums.length - 1;
        while (true) {
            int pivot = nums[lo + random.nextInt(hi - lo + 1)];
            // three parts: [lo, lt) smaller than the pivot, [lt, gt] equal, (gt, hi] larger
            int lt = lo;
            int i = lo;
            int gt = hi;
            while (i <= gt) {
                if (nums[i] < pivot) {
                    swap(nums, lt++, i++);
                } else if (nums[i] > pivot) {
                    swap(nums, i, gt--);
                } else {
                    i++;
                }
            }
            if (target < lt) {
                hi = lt - 1;
            } else if (target > gt) {
                lo = gt + 1;
            } else {
                return pivot; // equal values keep the whole middle part in one step
            }
        }
    }

    private static void swap(int[] a, int i, int j) {
        int t = a[i];
        a[i] = a[j];
        a[j] = t;
    }
}
