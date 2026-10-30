import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Reports the k-th largest value seen so far as values keep arriving. */
public class KthLargest {
    private final int k;
    private final List<Integer> values = new ArrayList<>();

    /** Starts with the values in nums; nums holds at least k - 1 values. */
    KthLargest(int k, int[] nums) {
        this.k = k;
        for (int value : nums) {
            values.add(value);
        }
    }

    /** Adds a value and returns the k-th largest of all values so far, counting repeats. */
    int add(int value) {
        values.add(value);
        int[] sorted = new int[values.size()];
        for (int i = 0; i < sorted.length; i++) {
            sorted[i] = values.get(i);
        }
        Arrays.sort(sorted);
        return sorted[sorted.length - k];
    }
}
