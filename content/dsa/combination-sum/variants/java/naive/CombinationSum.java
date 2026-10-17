import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Finds the distinct combinations of values, each usable any number of times, that reach a target. */
public class CombinationSum {

    /** Returns each distinct combination of candidates that adds up to target once. */
    static List<List<Integer>> find(int[] candidates, int target) {
        Set<List<Integer>> seen = new HashSet<>();
        build(candidates, target, new ArrayList<>(), seen);
        return new ArrayList<>(seen);
    }

    // Tries every value at every step, so each combination is reached once per ordering of it.
    private static void build(int[] candidates, int left, List<Integer> sequence, Set<List<Integer>> seen) {
        if (left == 0) {
            List<Integer> combination = new ArrayList<>(sequence);
            combination.sort(null); // every ordering of one combination sorts to the same list
            seen.add(combination);
            return;
        }
        for (int value : candidates) {
            if (value <= left) {
                sequence.add(value);
                build(candidates, left - value, sequence, seen);
                sequence.remove(sequence.size() - 1);
            }
        }
    }
}
