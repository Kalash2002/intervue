import static kit.Check.assertEquals;
import static kit.Check.assertTrue;

import java.util.Arrays;
import java.util.Random;
import kit.Test;

public class Phase1Tests {

    // Built once per test JVM: 10,000 piles of 1 to 10^9 bananas, seeded.
    private static final int[] BIG = new int[10_000];

    static {
        Random random = new Random(20261016L);
        for (int i = 0; i < BIG.length; i++) {
            BIG[i] = 1 + random.nextInt(1_000_000_000);
        }
    }

    /** Hours needed at a speed, counted in long so the check itself cannot overflow. */
    private static long hoursAt(int[] piles, long speed) {
        long total = 0;
        for (int bananas : piles) {
            total += (bananas + speed - 1) / speed;
        }
        return total;
    }

    @Test(order = 1)
    void sample() {
        int[] piles = {7, 15, 4, 11};
        assertEquals(6, EatingSpeed.minSpeed(piles, 8));
        assertEquals(15, EatingSpeed.minSpeed(piles, 4));
    }

    @Test(order = 2)
    void oneHourPerPile() {
        assertEquals(9, EatingSpeed.minSpeed(new int[] {3, 9, 2}, 3));
    }

    @Test(order = 3)
    void plentyOfTime() {
        assertEquals(1, EatingSpeed.minSpeed(new int[] {5, 8, 2}, 1000));
        assertEquals(1, EatingSpeed.minSpeed(new int[] {5, 8, 2}, 15));
        assertEquals(2, EatingSpeed.minSpeed(new int[] {5, 8, 2}, 14));
    }

    @Test(order = 4)
    void singlePile() {
        int[] one = {20};
        assertEquals(3, EatingSpeed.minSpeed(one, 7));
        assertEquals(1, EatingSpeed.minSpeed(one, 20));
        assertEquals(20, EatingSpeed.minSpeed(one, 1));
    }

    @Test(order = 5)
    void partialHoursRoundUp() {
        // At 4 bananas an hour each pile takes 3 hours (9 hours in all); rounding down would say 2 each.
        assertEquals(5, EatingSpeed.minSpeed(new int[] {9, 9, 9}, 6));
        assertEquals(3, EatingSpeed.minSpeed(new int[] {9, 9, 9}, 9));
    }

    @Test(order = 6)
    void hoursPastTheIntRange() {
        // At speed 1 these need 3 * 10^9 hours, which an int count wraps to a negative number.
        int[] piles = {1_000_000_000, 1_000_000_000, 1_000_000_000};
        assertEquals(2, EatingSpeed.minSpeed(piles, 2_000_000_000));
        int[] manyPiles = new int[10_000];
        Arrays.fill(manyPiles, 1_000_000_000);
        assertEquals(5000, EatingSpeed.minSpeed(manyPiles, 2_000_000_000));
    }

    @Test(order = 7)
    void largestSpeed() {
        assertEquals(1_000_000, EatingSpeed.minSpeed(new int[] {1_000_000, 1}, 2));
    }

    @Test(order = 8, timeoutMs = 500)
    void tenThousandPilesUnderSeveralTimeLimits() {
        int[] limits = {10_000, 10_007, 12_500, 40_000, 1_000_000};
        for (int hours : limits) {
            int speed = EatingSpeed.minSpeed(BIG, hours);
            assertTrue(speed >= 1 && hoursAt(BIG, speed) <= hours, "speed " + speed + " misses " + hours + " hours");
            assertTrue(speed == 1 || hoursAt(BIG, speed - 1) > hours, "speed " + speed + " is not the slowest for " + hours + " hours");
        }
    }
}
