import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Finds the most frequent values. */
public class TopKFrequent {

    /** Returns the k values that occur most often, in any order. */
    static int[] topK(int[] nums, int k) {
        Map<Integer, Integer> counts = new HashMap<>();
        for (int value : nums) {
            counts.merge(value, 1, Integer::sum);
        }
        // buckets.get(c) holds the values that occur exactly c times; a count is at most nums.length
        List<List<Integer>> buckets = new ArrayList<>();
        for (int c = 0; c <= nums.length; c++) {
            buckets.add(null);
        }
        for (Map.Entry<Integer, Integer> entry : counts.entrySet()) {
            int c = entry.getValue();
            if (buckets.get(c) == null) {
                buckets.set(c, new ArrayList<>());
            }
            buckets.get(c).add(entry.getKey());
        }
        int[] result = new int[k];
        int filled = 0;
        for (int c = nums.length; c >= 1 && filled < k; c--) { // highest counts first
            if (buckets.get(c) == null) {
                continue;
            }
            for (int value : buckets.get(c)) {
                if (filled < k) {
                    result[filled++] = value;
                }
            }
        }
        return result;
    }
}
