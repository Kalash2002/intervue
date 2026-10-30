import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Finds the distinct combinations of values, each usable any number of times, that reach a target. */
public class CombinationSum {

    /** Returns each distinct combination of candidates that adds up to target once. */
    static List<List<Integer>> find(int[] candidates, int target) {
        int[] sorted = candidates.clone();
        Arrays.sort(sorted); // ascending, so the first value that is too big ends the loop
        List<List<Integer>> result = new ArrayList<>();
        extend(sorted, 0, target, new ArrayList<>(), result);
        return result;
    }

    // Adds values from sorted[start] onward to chosen; left is what chosen still lacks.
    private static void extend(int[] sorted, int start, int left, List<Integer> chosen, List<List<Integer>> result) {
        if (left == 0) {
            result.add(new ArrayList<>(chosen));
            return;
        }
        for (int i = start; i < sorted.length && sorted[i] <= left; i++) {
            chosen.add(sorted[i]);
            extend(sorted, i, left - sorted[i], chosen, result); // i, not i + 1: the same value may come again
            chosen.remove(chosen.size() - 1);
        }
    }
}
