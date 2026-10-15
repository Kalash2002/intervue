/** Computes, for each position, the product of all the other numbers. */
public class Products {

    /** Returns out where out[i] is the product of every nums[j] with j != i. */
    static int[] exceptSelf(int[] nums) {
        int n = nums.length;
        int[] out = new int[n];
        for (int i = 0; i < n; i++) {
            int product = 1;
            for (int j = 0; j < n; j++) {
                if (j != i) {
                    product *= nums[j];
                }
            }
            out[i] = product;
        }
        return out;
    }
}
