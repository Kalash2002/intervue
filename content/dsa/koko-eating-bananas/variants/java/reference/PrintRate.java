/** The slowest printer rate that still meets a deadline. */
public class PrintRate {

    static int minRate(int[] jobs, int hours) {
        int low = 1;
        int high = 1;
        for (int pages : jobs) {
            high = Math.max(high, pages); // at the largest job's size every job takes one hour
        }
        while (low < high) {
            int middle = (low + high) >>> 1;
            if (hoursAt(jobs, middle) <= hours) {
                high = middle; // fast enough: the answer is middle or slower
            } else {
                low = middle + 1;
            }
        }
        return low;
    }

    private static long hoursAt(int[] jobs, int rate) {
        long total = 0; // up to 10^4 jobs of 10^9 hours each: too big for an int
        for (int pages : jobs) {
            total += (pages - 1) / rate + 1; // whole hours, rounded up
        }
        return total;
    }
}
