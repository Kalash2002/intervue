import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Finds the distinct combinations of values, each position used at most once, that reach a target. */
public class CombinationSumOnce {

    /** Returns each distinct combination of candidates that adds up to target once. */
    static List<List<Integer>> find(int[] candidates, int target) {
        int[] sorted = candidates.clone();
        Arrays.sort(sorted); // equal values side by side, and the first value too big ends a loop
        List<List<Integer>> result = new ArrayList<>();
        extend(sorted, 0, target, new ArrayList<>(), result);
        return result;
    }

    // Adds one more value from sorted[start] onward to chosen; left is what chosen still lacks.
    private static void extend(int[] sorted, int start, int left, List<Integer> chosen, List<List<Integer>> result) {
        if (left == 0) {
            result.add(new ArrayList<>(chosen));
            return;
        }
        for (int i = start; i < sorted.length && sorted[i] <= left; i++) {
            if (i > start && sorted[i] == sorted[i - 1]) {
                continue; // the same value in the same slot would rebuild combinations already listed
            }
            chosen.add(sorted[i]);
            extend(sorted, i + 1, left - sorted[i], chosen, result); // i + 1: each position once
            chosen.remove(chosen.size() - 1);
        }
    }
}
