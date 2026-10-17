import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/** Lists the distinct subsets of an array that may hold repeated values. */
public class SubsetsWithRepeats {

    /** Returns each distinct subset of nums once, the empty one included. */
    static List<List<Integer>> all(int[] nums) {
        int[] sorted = nums.clone();
        Arrays.sort(sorted); // equal subsets then come out as equal lists
        Set<List<Integer>> seen = new LinkedHashSet<>();
        for (int mask = 0; mask < (1 << sorted.length); mask++) { // every choice of positions
            List<Integer> subset = new ArrayList<>();
            for (int i = 0; i < sorted.length; i++) {
                if ((mask & (1 << i)) != 0) {
                    subset.add(sorted[i]);
                }
            }
            seen.add(subset); // a repeat of a subset already seen is dropped here
        }
        return new ArrayList<>(seen);
    }
}
