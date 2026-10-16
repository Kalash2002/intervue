import java.util.Arrays;

/** The median of two sorted arrays taken together. */
public class SortedMedian {

    static double median(int[] a, int[] b) {
        int[] all = new int[a.length + b.length];
        System.arraycopy(a, 0, all, 0, a.length);
        System.arraycopy(b, 0, all, a.length, b.length);
        Arrays.sort(all);
        int k = all.length;
        if (k % 2 == 1) {
            return all[k / 2];
        }
        return ((long) all[k / 2 - 1] + all[k / 2]) / 2.0;
    }
}
