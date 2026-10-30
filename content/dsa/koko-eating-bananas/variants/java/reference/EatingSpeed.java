/** The slowest eating speed that still clears every pile in time. */
public class EatingSpeed {

    static int minSpeed(int[] piles, int hours) {
        int low = 1;
        int high = 1;
        for (int bananas : piles) {
            high = Math.max(high, bananas); // at the largest pile's size every pile takes one hour
        }
        while (low < high) {
            int middle = (low + high) >>> 1;
            if (hoursAt(piles, middle) <= hours) {
                high = middle; // fast enough: the answer is middle or slower
            } else {
                low = middle + 1;
            }
        }
        return low;
    }

    private static long hoursAt(int[] piles, int speed) {
        long total = 0; // up to 10^4 piles of 10^9 hours each: too big for an int
        for (int bananas : piles) {
            total += (bananas - 1) / speed + 1; // whole hours, rounded up
        }
        return total;
    }
}
