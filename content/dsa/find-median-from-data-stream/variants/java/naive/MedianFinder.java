import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Reports the median of all numbers added so far. */
public class MedianFinder {
    private final List<Integer> numbers = new ArrayList<>();

    /** Adds a number to the data. */
    void addNum(int num) {
        numbers.add(num);
    }

    /** Returns the median of the numbers added so far; at least one has been added. */
    double findMedian() {
        int[] sorted = new int[numbers.size()];
        for (int i = 0; i < sorted.length; i++) {
            sorted[i] = numbers.get(i);
        }
        Arrays.sort(sorted);
        int n = sorted.length;
        if (n % 2 == 1) {
            return sorted[n / 2];
        }
        return ((long) sorted[n / 2 - 1] + sorted[n / 2]) / 2.0;
    }
}
