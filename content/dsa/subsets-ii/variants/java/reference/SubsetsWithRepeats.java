import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Lists the distinct subsets of an array that may hold repeated values. */
public class SubsetsWithRepeats {

    /** Returns each distinct subset of nums once, the empty one included. */
    static List<List<Integer>> all(int[] nums) {
        int[] sorted = nums.clone();
        Arrays.sort(sorted); // equal values side by side, so a repeat is the value just before
        List<List<Integer>> result = new ArrayList<>();
        extend(sorted, 0, new ArrayList<>(), result);
        return result;
    }

    // chosen is one distinct subset; extends it with one more value from sorted[start] onward.
    private static void extend(int[] sorted, int start, List<Integer> chosen, List<List<Integer>> result) {
        result.add(new ArrayList<>(chosen));
        for (int i = start; i < sorted.length; i++) {
            if (i > start && sorted[i] == sorted[i - 1]) {
                continue; // the same value in the same slot would rebuild subsets already listed
            }
            chosen.add(sorted[i]);
            extend(sorted, i + 1, chosen, result);
            chosen.remove(chosen.size() - 1);
        }
    }
}
