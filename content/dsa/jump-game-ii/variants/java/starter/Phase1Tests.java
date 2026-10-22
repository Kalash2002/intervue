import static kit.Check.assertEquals;

import java.util.Arrays;
import java.util.Random;
import kit.Test;

public class Phase1Tests {

    private static void expect(int want, int... range) {
        String input = range.length <= 50 ? Arrays.toString(range) : range.length + " stations";
        assertEquals(want, Relay.fewestHops(range.clone()), "range = " + input);
    }

    @Test(order = 1)
    void sample() {
        expect(3, 3, 6, 0, 1, 3, 0, 0, 1, 0);
        expect(2, 3, 0, 0, 6, 1, 0);
        expect(0, 5);
    }

    @Test(order = 2)
    void forwardingAsFarAsPossibleIsNotFewest() {
        expect(2, 3, 4, 6, 1, 2, 0, 1, 0); // 0 -> 2 -> 7; the farthest hop from 0 lands on 3 and takes 4
    }

    @Test(order = 3)
    void theLargestRangeIsNotAlwaysBest() {
        expect(2, 4, 3, 2, 0, 1, 6); // 0 -> 4 -> 5; picking the largest range goes 0 -> 1 -> 2 -> 4 -> 5
    }

    @Test(order = 4)
    void aStretchThatEndsOnTheLastStation() {
        expect(1, 2, 1, 1); // the first hop lands exactly on the last station
        expect(3, 1, 1, 1, 1);
    }

    @Test(order = 5)
    void aRangePastTheEndIsFine() {
        expect(1, 6, 0, 0);
        expect(2, 1, 9, 0, 0);
    }

    @Test(order = 6)
    void oneStation() {
        expect(0, 0);
        expect(0, 100_000);
    }

    @Test(order = 7)
    void randomShortRows() {
        Random random = new Random(1430);
        int checked = 0;
        while (checked < 300) {
            int[] range = new int[1 + random.nextInt(12)];
            for (int i = 0; i < range.length; i++) {
                range[i] = random.nextInt(5);
            }
            int want = everyRoute(range, 0);
            if (want == Integer.MAX_VALUE) {
                continue; // the last station is out of reach, which the constraints rule out
            }
            expect(want, range);
            checked++;
        }
    }

    /** Fewest hops from station i over every route; MAX_VALUE when none gets there. */
    private static int everyRoute(int[] range, int i) {
        if (i == range.length - 1) {
            return 0;
        }
        int best = Integer.MAX_VALUE;
        for (int step = 1; step <= range[i] && i + step < range.length; step++) {
            int rest = everyRoute(range, i + step);
            if (rest != Integer.MAX_VALUE) {
                best = Math.min(best, rest + 1);
            }
        }
        return best;
    }

    @Test(order = 8)
    void longRowsOfShortRanges() {
        int[] range = new int[100_000];
        Arrays.fill(range, 1);
        expect(99_999, range); // one station per hop
        Arrays.fill(range, 2);
        expect(50_000, range); // two stations per hop, 99,999 stations to cover
    }

    @Test(order = 9, timeoutMs = 150)
    void everyStationAimsAtTheSameOne() {
        // Stations 0 to 99,997 all reach exactly station 99,998, and only it reaches the last.
        int n = 100_000;
        int[] range = new int[n];
        for (int i = 0; i < n - 2; i++) {
            range[i] = n - 2 - i;
        }
        range[n - 2] = 1;
        expect(2, range);
    }

    @Test(order = 10, timeoutMs = 150)
    void longEqualRanges() {
        // Every station reaches 30,000 ahead: 3 hops get as far as station 90,000, so the last
        // station, 99,999, needs a fourth.
        int[] range = new int[100_000];
        Arrays.fill(range, 30_000);
        expect(4, range);
    }
}
