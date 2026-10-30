import java.util.Arrays;
import java.util.Random;

/** Picks the points nearest to the origin. */
public class ClosestPoints {

    /** Returns the k points closest to (0, 0), in any order; each point is {x, y}. */
    static int[][] kClosest(int[][] points, int k) {
        int[][] p = points.clone();
        Random random = new Random();
        int lo = 0;
        int hi = p.length - 1;
        while (lo < hi) {
            long pivot = squared(p[lo + random.nextInt(hi - lo + 1)]);
            // three parts: [lo, lt) closer than the pivot, [lt, gt] as close, (gt, hi] farther
            int lt = lo;
            int i = lo;
            int gt = hi;
            while (i <= gt) {
                long d = squared(p[i]);
                if (d < pivot) {
                    swap(p, lt++, i++);
                } else if (d > pivot) {
                    swap(p, i, gt--);
                } else {
                    i++;
                }
            }
            if (k <= lt) {
                hi = lt - 1; // the k closest all lie in the closer part
            } else if (k <= gt + 1) {
                break; // the first k slots hold the answer
            } else {
                lo = gt + 1;
            }
        }
        return Arrays.copyOf(p, k);
    }

    private static long squared(int[] point) {
        return (long) point[0] * point[0] + (long) point[1] * point[1]; // up to 2 * 10^10: past int
    }

    private static void swap(int[][] p, int a, int b) {
        int[] t = p[a];
        p[a] = p[b];
        p[b] = t;
    }
}
