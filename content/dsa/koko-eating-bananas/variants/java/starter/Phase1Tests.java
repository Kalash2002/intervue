import static kit.Check.assertEquals;
import static kit.Check.assertTrue;

import java.util.Arrays;
import java.util.Random;
import kit.Test;

public class Phase1Tests {

    // Built once per test JVM: 10,000 jobs of 1 to 10^9 pages, seeded.
    private static final int[] BIG = new int[10_000];

    static {
        Random random = new Random(20261016L);
        for (int i = 0; i < BIG.length; i++) {
            BIG[i] = 1 + random.nextInt(1_000_000_000);
        }
    }

    /** Hours needed at a rate, counted in long so the check itself cannot overflow. */
    private static long hoursAt(int[] jobs, long rate) {
        long total = 0;
        for (int pages : jobs) {
            total += (pages + rate - 1) / rate;
        }
        return total;
    }

    @Test(order = 1)
    void sample() {
        int[] jobs = {7, 15, 4, 11};
        assertEquals(6, PrintRate.minRate(jobs, 8));
        assertEquals(15, PrintRate.minRate(jobs, 4));
    }

    @Test(order = 2)
    void oneHourPerJob() {
        assertEquals(9, PrintRate.minRate(new int[] {3, 9, 2}, 3));
    }

    @Test(order = 3)
    void plentyOfTime() {
        assertEquals(1, PrintRate.minRate(new int[] {5, 8, 2}, 1000));
        assertEquals(1, PrintRate.minRate(new int[] {5, 8, 2}, 15));
        assertEquals(2, PrintRate.minRate(new int[] {5, 8, 2}, 14));
    }

    @Test(order = 4)
    void singleJob() {
        int[] one = {20};
        assertEquals(3, PrintRate.minRate(one, 7));
        assertEquals(1, PrintRate.minRate(one, 20));
        assertEquals(20, PrintRate.minRate(one, 1));
    }

    @Test(order = 5)
    void partialHoursRoundUp() {
        // At 4 pages an hour each job takes 3 hours (9 hours in all); rounding down would say 2 each.
        assertEquals(5, PrintRate.minRate(new int[] {9, 9, 9}, 6));
        assertEquals(3, PrintRate.minRate(new int[] {9, 9, 9}, 9));
    }

    @Test(order = 6)
    void hoursPastTheIntRange() {
        // At rate 1 these need 3 * 10^9 hours, which an int count wraps to a negative number.
        int[] jobs = {1_000_000_000, 1_000_000_000, 1_000_000_000};
        assertEquals(2, PrintRate.minRate(jobs, 2_000_000_000));
        int[] many = new int[10_000];
        Arrays.fill(many, 1_000_000_000);
        assertEquals(5000, PrintRate.minRate(many, 2_000_000_000));
    }

    @Test(order = 7)
    void largestRate() {
        assertEquals(1_000_000, PrintRate.minRate(new int[] {1_000_000, 1}, 2));
    }

    @Test(order = 8, timeoutMs = 500)
    void tenThousandJobsUnderSeveralDeadlines() {
        int[] deadlines = {10_000, 10_007, 12_500, 40_000, 1_000_000};
        for (int hours : deadlines) {
            int rate = PrintRate.minRate(BIG, hours);
            assertTrue(rate >= 1 && hoursAt(BIG, rate) <= hours, "rate " + rate + " misses " + hours + " hours");
            assertTrue(rate == 1 || hoursAt(BIG, rate - 1) > hours, "rate " + rate + " is not the slowest for " + hours + " hours");
        }
    }
}
