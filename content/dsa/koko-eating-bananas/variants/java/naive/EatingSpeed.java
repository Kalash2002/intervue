/** The slowest eating speed that still clears every pile in time. */
public class EatingSpeed {

    static int minSpeed(int[] piles, int hours) {
        for (int speed = 1; ; speed++) { // ends by the largest pile's size, where every pile takes one hour
            if (hoursAt(piles, speed) <= hours) {
                return speed;
            }
        }
    }

    private static long hoursAt(int[] piles, int speed) {
        long total = 0;
        for (int bananas : piles) {
            total += (bananas - 1) / speed + 1;
        }
        return total;
    }
}
