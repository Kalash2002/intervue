import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Finds the distinct combinations of values, each position used at most once, that reach a target. */
public class CombinationSumOnce {

    /** Returns each distinct combination of candidates that adds up to target once. */
    static List<List<Integer>> find(int[] candidates, int target) {
        Set<List<Integer>> seen = new HashSet<>();
        decide(candidates, 0, target, new ArrayList<>(), seen);
        return new ArrayList<>(seen);
    }

    // Takes or leaves each position in turn; equal values at different positions are tried separately.
    private static void decide(int[] candidates, int i, int left, List<Integer> chosen, Set<List<Integer>> seen) {
        if (left == 0) {
            List<Integer> combination = new ArrayList<>(chosen);
            combination.sort(null); // the same values from other positions sort to the same list
            seen.add(combination);
            return;
        }
        if (i == candidates.length) {
            return;
        }
        if (candidates[i] <= left) { // values are positive, so a value above left can never fit
            chosen.add(candidates[i]);
            decide(candidates, i + 1, left - candidates[i], chosen, seen);
            chosen.remove(chosen.size() - 1);
        }
        decide(candidates, i + 1, left, chosen, seen);
    }
}
