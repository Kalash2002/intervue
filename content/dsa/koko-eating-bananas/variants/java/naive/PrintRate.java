/** The slowest printer rate that still meets a deadline. */
public class PrintRate {

    static int minRate(int[] jobs, int hours) {
        for (int rate = 1; ; rate++) { // ends by the largest job's size, where every job takes one hour
            if (hoursAt(jobs, rate) <= hours) {
                return rate;
            }
        }
    }

    private static long hoursAt(int[] jobs, int rate) {
        long total = 0;
        for (int pages : jobs) {
            total += (pages - 1) / rate + 1;
        }
        return total;
    }
}
