import static kit.Check.assertEquals;

import java.util.Arrays;
import java.util.Random;
import kit.Test;

public class Phase1Tests {

    private static void expect(int want, int[] charge, int[] drain) {
        String input =
                charge.length <= 50
                        ? "charge = " + Arrays.toString(charge) + ", drain = " + Arrays.toString(drain)
                        : charge.length + " stops";
        assertEquals(want, Shuttle.startStop(charge.clone(), drain.clone()), input);
    }

    @Test(order = 1)
    void sample() {
        expect(3, new int[] {6, 9, 0, 5, 6, 5}, new int[] {8, 9, 7, 3, 1, 2});
        expect(-1, new int[] {4, 2, 6, 1}, new int[] {3, 5, 2, 4});
        expect(0, new int[] {2, 5, 1}, new int[] {1, 3, 2});
        expect(0, new int[] {4}, new int[] {4});
    }

    @Test(order = 2)
    void theStopWithTheMostChargeIsNotTheStart() {
        expect(0, new int[] {1, 8, 2, 3}, new int[] {0, 9, 1, 2}); // stop 1 runs out on its own leg
    }

    @Test(order = 3)
    void theLargestGainIsNotTheStart() {
        // stop 0 gains 4 on its own leg but runs out on the leg from stop 1
        expect(2, new int[] {6, 1, 7, 2}, new int[] {2, 9, 4, 0});
    }

    @Test(order = 4)
    void notEnoughChargeOverall() {
        // from stop 1 the battery reads 1 and 2 up to the end of the list, then runs out
        expect(-1, new int[] {1, 3, 3}, new int[] {4, 2, 2});
    }

    @Test(order = 5)
    void theLowestOfSeveralStarts() {
        // stops 0, 1 and 3 all work; from stop 0 the battery is empty on arrival at stop 1
        expect(0, new int[] {3, 5, 1, 4}, new int[] {3, 3, 3, 3});
    }

    @Test(order = 6)
    void arrivingEmptyIsAllowed() {
        // from stop 2 the battery reads 1, 1 and 0: back at stop 2 with nothing left
        expect(2, new int[] {2, 0, 3}, new int[] {2, 1, 2});
    }

    @Test(order = 7)
    void oneStop() {
        expect(-1, new int[] {3}, new int[] {5});
        expect(0, new int[] {0}, new int[] {0});
        expect(0, new int[] {6}, new int[] {1});
    }

    @Test(order = 8)
    void randomShortLoops() {
        Random random = new Random(1425);
        for (int round = 0; round < 300; round++) {
            int n = 1 + random.nextInt(8);
            int[] charge = new int[n];
            int[] drain = new int[n];
            for (int i = 0; i < n; i++) {
                charge[i] = random.nextInt(6);
                drain[i] = random.nextInt(6);
            }
            expect(everyStart(charge, drain), charge, drain);
        }
    }

    /** Drives the loop from every stop in turn and returns the first that gets round. */
    private static int everyStart(int[] charge, int[] drain) {
        int n = charge.length;
        for (int start = 0; start < n; start++) {
            int battery = 0;
            boolean round = true;
            for (int k = 0; k < n && round; k++) {
                int stop = (start + k) % n;
                battery += charge[stop] - drain[stop];
                round = battery >= 0;
            }
            if (round) {
                return start;
            }
        }
        return -1;
    }

    @Test(order = 9, timeoutMs = 200)
    void aShortfallHalfWayRound() {
        // Every stop gives exactly what its leg uses, except stop 50,000, which is 1 short. The
        // loop as a whole is 1 short, and every start runs out on the leg from stop 50,000.
        int n = 100_000;
        int[] charge = new int[n];
        int[] drain = new int[n];
        Arrays.fill(charge, 10_000);
        Arrays.fill(drain, 10_000);
        charge[50_000] = 9_999;
        expect(-1, charge, drain);
    }

    @Test(order = 10, timeoutMs = 200)
    void onlyTheLastStopWorks() {
        // Stops 49,999 and 99,998 are each 1 short and stop 99,999 gives 2 more than its leg
        // uses. Every start before 50,000 runs out on the leg from stop 49,999, every start from
        // 50,000 to 99,998 on the leg from stop 99,998; from stop 99,999 the battery never drops
        // below zero.
        int n = 100_000;
        int[] charge = new int[n];
        int[] drain = new int[n];
        Arrays.fill(charge, 10_000);
        Arrays.fill(drain, 10_000);
        charge[49_999] = 9_999;
        charge[99_998] = 9_999;
        drain[99_999] = 9_998;
        expect(99_999, charge, drain);
    }
}
