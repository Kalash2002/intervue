import java.util.Arrays;

/** Plans the requests a telescope serves, one at a time. */
public class Telescope {

    /** Returns the fewest requests to cancel so that no two of the rest overlap; each is [start, end). */
    static int minCancellations(int[][] requests) {
        int[][] byEnd = requests.clone();
        Arrays.sort(byEnd, (a, b) -> Integer.compare(a[1], b[1]));
        int n = byEnd.length;
        int[] chain = new int[n]; // chain[i]: most requests kept when byEnd[i] is the last of them
        int most = 0;
        for (int i = 0; i < n; i++) {
            chain[i] = 1;
            for (int j = 0; j < i; j++) {
                if (byEnd[j][1] <= byEnd[i][0]) { // j ends by the time i starts
                    chain[i] = Math.max(chain[i], chain[j] + 1);
                }
            }
            most = Math.max(most, chain[i]);
        }
        return n - most;
    }
}
