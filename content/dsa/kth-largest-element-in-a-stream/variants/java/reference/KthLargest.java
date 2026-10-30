import java.util.PriorityQueue;

/** Reports the k-th largest value seen so far as values keep arriving. */
public class KthLargest {
    private final int k;
    private final PriorityQueue<Integer> top = new PriorityQueue<>(); // the k largest so far, smallest on top

    /** Starts with the values in nums; nums holds at least k - 1 values. */
    KthLargest(int k, int[] nums) {
        this.k = k;
        for (int value : nums) {
            add(value);
        }
    }

    /** Adds a value and returns the k-th largest of all values so far, counting repeats. */
    int add(int value) {
        if (top.size() < k) {
            top.add(value);
        } else if (value > top.peek()) { // it beats the current k-th largest, which drops out
            top.poll();
            top.add(value);
        }
        return top.peek();
    }
}
