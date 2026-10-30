/** Picks the most values, in order, that strictly rise. */
public class Rising {

    /** Returns the length of the longest strictly increasing subsequence of nums. */
    static int longest(int[] nums) {
        int[] endingAt = new int[nums.length]; // endingAt[i]: longest rising pick ending at i
        int best = 0;
        for (int i = 0; i < nums.length; i++) {
            endingAt[i] = 1; // nums[i] on its own
            for (int j = 0; j < i; j++) {
                if (nums[j] < nums[i] && endingAt[j] + 1 > endingAt[i]) { // strictly smaller before it
                    endingAt[i] = endingAt[j] + 1;
                }
            }
            best = Math.max(best, endingAt[i]);
        }
        return best;
    }
}
