import java.util.ArrayList;
import java.util.List;

/** Finds the most frequent values. */
public class TopKFrequent {

    /** Returns the k values that occur most often, in any order. */
    static int[] topK(int[] nums, int k) {
        List<Integer> distinct = new ArrayList<>();
        List<Integer> counts = new ArrayList<>();
        for (int value : nums) {
            if (distinct.contains(value)) { // linear search per element
                continue;
            }
            int count = 0;
            for (int other : nums) { // a full pass to count one value
                if (other == value) {
                    count++;
                }
            }
            distinct.add(value);
            counts.add(count);
        }
        int[] result = new int[k];
        for (int round = 0; round < k; round++) { // pick the largest count k times
            int best = 0;
            for (int i = 1; i < counts.size(); i++) {
                if (counts.get(i) > counts.get(best)) {
                    best = i;
                }
            }
            result[round] = distinct.get(best);
            counts.set(best, -1);
        }
        return result;
    }
}
