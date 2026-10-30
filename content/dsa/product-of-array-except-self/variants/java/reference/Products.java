/** Computes, for each position, the product of all the other numbers. */
public class Products {

    /** Returns out where out[i] is the product of every nums[j] with j != i. */
    static int[] exceptSelf(int[] nums) {
        int n = nums.length;
        int[] out = new int[n];
        int left = 1; // product of everything before the current slot
        for (int i = 0; i < n; i++) {
            out[i] = left;
            left *= nums[i];
        }
        int right = 1; // product of everything after the current slot
        for (int i = n - 1; i >= 0; i--) {
            out[i] *= right;
            right *= nums[i];
        }
        return out;
    }
}
