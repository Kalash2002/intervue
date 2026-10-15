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
        List<int[]> work = new ArrayList<>();
        for (int[] range : ranges) {
            work.add(new int[] {range[0], range[1]});
        }
        boolean changed = true;
        while (changed) { // keep merging pairs until a whole pass finds nothing to merge
            changed = false;
            for (int i = 0; i < work.size(); i++) {
                for (int j = i + 1; j < work.size(); j++) {
                    int[] a = work.get(i);
                    int[] b = work.get(j);
                    if (a[0] <= b[1] && b[0] <= a[1]) {
                        a[0] = Math.min(a[0], b[0]);
                        a[1] = Math.max(a[1], b[1]);
                        work.remove(j);
                        j--;
                        changed = true;
                    }
                }
            }
        }
        int[][] result = work.toArray(new int[0][]);
        Arrays.sort(result, (a, b) -> Integer.compare(a[0], b[0]));
        return result;
    }
}
