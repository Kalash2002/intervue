import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Works with ranges of time. */
public class Schedule {

    /**
     * Returns the ranges combined so that no two overlap or touch, sorted by start.
     * The input is in no particular order.
     */
    static int[][] mergeRanges(int[][] ranges) {
        int[][] sorted = ranges.clone();
        Arrays.sort(sorted, (a, b) -> Integer.compare(a[0], b[0]));
        List<int[]> merged = new ArrayList<>();
        for (int[] range : sorted) {
            int[] open = merged.isEmpty() ? null : merged.get(merged.size() - 1);
            if (open != null && range[0] <= open[1]) {
                open[1] = Math.max(open[1], range[1]); // joins the open range
            } else {
                merged.add(new int[] {range[0], range[1]}); // a copy, so the input is never changed
            }
        }
        return merged.toArray(new int[0][]);
    }
}
