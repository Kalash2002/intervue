import static kit.Check.assertEquals;

import java.util.Random;
import kit.Test;

public class Phase1Tests {

    @Test(order = 1)
    void sample() {
        int[][] rides = {{2, 1, 5}, {3, 3, 7}};
        assertEquals(false, Shuttle.canCarry(rides, 4));
        assertEquals(true, Shuttle.canCarry(rides, 5));
    }

    @Test(order = 2)
    void noRequests() {
        assertEquals(true, Shuttle.canCarry(new int[][] {}, 1));
    }

    @Test(order = 3)
    void oneGroupLargerThanTheSeats() {
        assertEquals(false, Shuttle.canCarry(new int[][] {{5, 0, 3}}, 4));
        assertEquals(true, Shuttle.canCarry(new int[][] {{5, 0, 3}}, 5));
    }

    @Test(order = 4)
    void groupLeavesAsAnotherBoards() {
        assertEquals(true, Shuttle.canCarry(new int[][] {{4, 2, 6}, {4, 6, 9}}, 4));
        assertEquals(false, Shuttle.canCarry(new int[][] {{4, 2, 7}, {4, 6, 9}}, 4));
    }

    @Test(order = 5)
    void unsortedRequestsPeakTwice() {
        int[][] rides = {{2, 8, 10}, {3, 1, 9}, {2, 2, 4}};
        assertEquals(true, Shuttle.canCarry(rides, 5));
        assertEquals(false, Shuttle.canCarry(rides, 4));
    }

    @Test(order = 6)
    void identicalRequests() {
        int[][] rides = {{1, 0, 10}, {1, 0, 10}, {1, 0, 10}};
        assertEquals(true, Shuttle.canCarry(rides, 3));
        assertEquals(false, Shuttle.canCarry(rides, 2));
    }

    @Test(order = 7)
    void longRequestWithShortOnesInsideIt() {
        // peak is 5 during minutes 5 to 6 (3 + 2) and again during 9 to 12 (3 + 2)
        int[][] rides = {{3, 0, 1_000_000_000}, {2, 5, 6}, {2, 9, 12}, {1, 6, 9}};
        assertEquals(true, Shuttle.canCarry(rides, 5));
        assertEquals(false, Shuttle.canCarry(rides, 4));
    }

    @Test(order = 8, timeoutMs = 3000)
    void sixtyThousandShuffledRequests() {
        int n = 60_000;
        int[][] rides = new int[n][];
        for (int i = 0; i < n; i++) {
            rides[i] = new int[] {1, i, i + 1000}; // exactly 1,000 aboard at any minute from 999 on
        }
        Random random = new Random(33);
        for (int i = n - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            int[] swap = rides[i];
            rides[i] = rides[j];
            rides[j] = swap;
        }
        assertEquals(true, Shuttle.canCarry(rides, 1000));
        assertEquals(false, Shuttle.canCarry(rides, 999));
    }
}
