/** Counts the choices of signs that bring a row of numbers to a target. */
public class Signs {

    /** Returns how many choices of a plus or minus sign for each number make the signed numbers add up to target. */
    static long ways(int[] nums, int target) {
        return from(nums, 0, 0, target);
    }

    /** Choices of signs for the numbers from position i on that bring total to target. */
    private static long from(int[] nums, int i, int total, int target) {
        if (i == nums.length) {
            return total == target ? 1 : 0;
        }
        // both signs, even for a 0: +0 and -0 are different choices
        return from(nums, i + 1, total + nums[i], target) + from(nums, i + 1, total - nums[i], target);
    }
}
