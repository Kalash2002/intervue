import java.util.ArrayList;
import java.util.List;

/** Lists every subset of an array of distinct numbers. */
public class Subsets {

    /** Returns every subset of nums once, the empty one included. */
    static List<List<Integer>> all(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        decide(nums, 0, new ArrayList<>(), result);
        return result;
    }

    // Decides in or out for nums[i] onward; chosen holds the values taken so far.
    private static void decide(int[] nums, int i, List<Integer> chosen, List<List<Integer>> result) {
        if (i == nums.length) {
            result.add(new ArrayList<>(chosen)); // a copy: chosen keeps changing after this
            return;
        }
        chosen.add(nums[i]); // nums[i] in
        decide(nums, i + 1, chosen, result);
        chosen.remove(chosen.size() - 1); // take it back, then nums[i] out
        decide(nums, i + 1, chosen, result);
    }
}
