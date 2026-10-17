import static kit.Check.assertEquals;

import java.util.Arrays;
import java.util.Random;
import kit.Test;

public class Phase1Tests {

    @Test(order = 1)
    void sample() {
        assertEquals(9, StairFees.minCost(new int[] {4, 9, 2, 7, 3}));
        assertEquals(3, StairFees.minCost(new int[] {5, 1, 1, 8, 1, 6}));
        assertEquals(2, StairFees.minCost(new int[] {6, 2}));
    }

    @Test(order = 2)
    void twoStepsEitherWayRound() {
        assertEquals(2, StairFees.minCost(new int[] {2, 6}), "stand on step 0, then climb two");
        assertEquals(2, StairFees.minCost(new int[] {6, 2}), "climb two to step 1, then one");
    }

    @Test(order = 3)
    void freeSteps() {
        assertEquals(0, StairFees.minCost(new int[] {0, 0, 0, 0}));
        assertEquals(0, StairFees.minCost(new int[] {999, 0, 999, 0, 999, 0}));
    }

    @Test(order = 4)
    void theLandingIsAboveTheLastStep() {
        // the last step need not be stood on: 4, then two moves up to the landing
        assertEquals(4, StairFees.minCost(new int[] {3, 4, 50}));
    }

    @Test(order = 5)
    void theFirstMoveMaySkipStepZero() {
        assertEquals(1, StairFees.minCost(new int[] {50, 1, 1}));
    }

    @Test(order = 6)
    void theCheaperNextStepIsNotAlwaysRight() {
        // taking the cheaper of the next two steps each time pays 1 + 2 + 1 = 4; steps 1 and 3 cost 3
        assertEquals(3, StairFees.minCost(new int[] {1, 2, 100, 1, 1}));
    }

    @Test(order = 7)
    void randomShortStaircases() {
        Random random = new Random(707);
        for (int round = 0; round < 200; round++) {
            int[] fee = new int[2 + random.nextInt(11)];
            for (int i = 0; i < fee.length; i++) {
                fee[i] = random.nextInt(1000);
            }
            assertEquals(cheapestRoute(fee, -1), StairFees.minCost(fee), "fee = " + Arrays.toString(fee));
        }
    }

    /** Tries every route from the given position (-1 is the floor) to the landing. */
    private static int cheapestRoute(int[] fee, int at) {
        if (at >= fee.length) {
            return 0;
        }
        int paid = at < 0 ? 0 : fee[at];
        return paid + Math.min(cheapestRoute(fee, at + 1), cheapestRoute(fee, at + 2));
    }

    @Test(order = 8, timeoutMs = 1000)
    void aLongRepeatingStaircase() {
        // fees 1, 8, 3 repeated 333 times: skip every 8, so pay 1 + 3 = 4 per group
        int[] fee = new int[999];
        int[] pattern = {1, 8, 3};
        for (int i = 0; i < fee.length; i++) {
            fee[i] = pattern[i % 3];
        }
        assertEquals(1332, StairFees.minCost(fee));
    }

    @Test(order = 9, timeoutMs = 1000)
    void aLongEvenStaircase() {
        // 1000 steps of fee 7: two steps per move, 500 steps stood on
        int[] fee = new int[1000];
        Arrays.fill(fee, 7);
        assertEquals(3500, StairFees.minCost(fee));
    }
}
