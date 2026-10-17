import static kit.Check.assertEquals;

import kit.Test;

public class Phase1Tests {

    @Test(order = 1)
    void sample() {
        assertEquals(8L, Staircase.countWays(5));
        assertEquals(21L, Staircase.countWays(7));
        assertEquals(1L, Staircase.countWays(1));
    }

    @Test(order = 2)
    void twoStepsCanBeClimbedTwoWays() {
        assertEquals(2L, Staircase.countWays(2), "two singles, or one double");
    }

    @Test(order = 3)
    void theFirstTenStaircases() {
        long[] expected = {1, 2, 3, 5, 8, 13, 21, 34, 55, 89};
        for (int n = 1; n <= 10; n++) {
            assertEquals(expected[n - 1], Staircase.countWays(n), "n = " + n);
        }
    }

    @Test(order = 4)
    void mediumStaircases() {
        assertEquals(1_346_269L, Staircase.countWays(30));
        assertEquals(14_930_352L, Staircase.countWays(35));
    }

    @Test(order = 5, timeoutMs = 1000)
    void fiftyStepsNeedMoreThanAnInt() {
        // 20,365,011,074 is past Integer.MAX_VALUE; an int count wraps around
        assertEquals(20_365_011_074L, Staircase.countWays(50));
    }

    @Test(order = 6, timeoutMs = 1000)
    void theLargestStaircase() {
        assertEquals(7_540_113_804_746_346_429L, Staircase.countWays(91));
    }
}
