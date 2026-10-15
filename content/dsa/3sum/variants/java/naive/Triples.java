import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Finds the distinct groups of three values that add up to zero. */
public class Triples {

    /** Returns each distinct zero triple once, as three numbers in non-decreasing order. */
    static List<List<Integer>> zeroSum(int[] nums) {
        int[] a = nums.clone();
        Arrays.sort(a);
        Set<List<Integer>> seen = new HashSet<>();
        List<List<Integer>> result = new ArrayList<>();
        for (int i = 0; i < a.length; i++) {
            for (int j = i + 1; j < a.length; j++) {
                for (int k = j + 1; k < a.length; k++) {
                    if (a[i] + a[j] + a[k] == 0) {
                        List<Integer> triple = List.of(a[i], a[j], a[k]);
                        if (seen.add(triple)) {
                            result.add(triple);
                        }
                    }
                }
            }
        }
        return result;
    }
}
