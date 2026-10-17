import java.util.Collections;
import java.util.PriorityQueue;

/** Reports the median of all numbers added so far. */
public class MedianFinder {
    private final PriorityQueue<Integer> low = new PriorityQueue<>(Collections.reverseOrder()); // smaller half, largest on top
    private final PriorityQueue<Integer> high = new PriorityQueue<>(); // larger half, smallest on top

    /** Adds a number to the data. */
    void addNum(int num) {
        low.add(num);
        high.add(low.poll()); // the largest of the smaller half moves up, so every low <= every high
        if (high.size() > low.size()) {
            low.add(high.poll()); // keep low the same size as high, or one larger
        }
    }

    /** Returns the median of the numbers added so far; at least one has been added. */
    double findMedian() {
        if (low.size() > high.size()) {
            return low.peek();
        }
        return ((long) low.peek() + high.peek()) / 2.0; // long: the sum of two ints can overflow
    }
}
