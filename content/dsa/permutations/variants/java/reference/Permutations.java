import java.util.ArrayList;
import java.util.List;

/** Lists every ordering of an array of distinct numbers. */
public class Permutations {

    /** Returns every ordering of nums once. */
    static List<List<Integer>> all(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        place(nums, new boolean[nums.length], new ArrayList<>(), result);
        return result;
    }

    // Fills the next position of chosen with each value not used yet.
    private static void place(int[] nums, boolean[] used, List<Integer> chosen, List<List<Integer>> result) {
        if (chosen.size() == nums.length) {
            result.add(new ArrayList<>(chosen)); // a copy: chosen keeps changing after this
            return;
        }
        for (int i = 0; i < nums.length; i++) {
            if (used[i]) {
                continue;
            }
            used[i] = true;
            chosen.add(nums[i]);
            place(nums, used, chosen, result);
            chosen.remove(chosen.size() - 1); // undo both marks so the next value starts clean
            used[i] = false;
        }
    }
}
