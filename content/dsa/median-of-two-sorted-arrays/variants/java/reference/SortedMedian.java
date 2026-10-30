/** The median of two sorted arrays taken together. */
public class SortedMedian {

    static double median(int[] a, int[] b) {
        if (a.length > b.length) {
            return median(b, a); // search the shorter array, so the cut in the longer one always exists
        }
        int m = a.length;
        int n = b.length;
        int half = (m + n + 1) / 2; // the left side holds the middle value when the total is odd
        int low = 0;
        int high = m;
        while (low <= high) {
            int i = (low + high) >>> 1; // values taken from a for the left side
            int j = half - i; // the rest come from b
            // an empty side of a cut reads as minus or plus infinity; long, so the sum below cannot overflow
            long aLeft = i == 0 ? Long.MIN_VALUE : a[i - 1];
            long aRight = i == m ? Long.MAX_VALUE : a[i];
            long bLeft = j == 0 ? Long.MIN_VALUE : b[j - 1];
            long bRight = j == n ? Long.MAX_VALUE : b[j];
            if (aLeft > bRight) {
                high = i - 1; // too many from a
            } else if (bLeft > aRight) {
                low = i + 1; // too few from a
            } else {
                long leftMax = Math.max(aLeft, bLeft);
                if ((m + n) % 2 == 1) {
                    return leftMax;
                }
                long rightMin = Math.min(aRight, bRight);
                return (leftMax + rightMin) / 2.0; // a long sum: two large ints would overflow an int
            }
        }
        throw new IllegalArgumentException("the arrays are not sorted");
    }
}
