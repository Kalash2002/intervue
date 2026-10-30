import static kit.Check.assertEquals;

import java.util.Random;
import kit.Test;

public class Phase1Tests {

    @Test(order = 1)
    void sample() {
        assertEquals(1, Studios.busiest(2, new int[][] {{0, 5}, {1, 3}, {2, 4}}));
    }

    @Test(order = 2)
    void singleRequestGoesToRoomZero() {
        assertEquals(0, Studios.busiest(4, new int[][] {{3, 4}}));
    }

    @Test(order = 3)
    void singleRoom() {
        assertEquals(0, Studios.busiest(1, new int[][] {{0, 3}, {1, 2}, {5, 6}}));
    }

    @Test(order = 4)
    void lowestFreeRoomIsChosen() {
        // room 0 holds the long session; rooms 1 and 2 are free again by 5; room 1 takes 3 more
        int[][] bookings = {{0, 10}, {1, 3}, {2, 4}, {5, 6}, {7, 8}, {9, 12}};
        assertEquals(1, Studios.busiest(3, bookings));
    }

    @Test(order = 5)
    void tieGoesToTheLowerNumber() {
        // rooms 1 and 2 host two sessions each, room 0 one
        int[][] bookings = {{0, 100}, {1, 3}, {2, 4}, {5, 9}, {6, 8}};
        assertEquals(1, Studios.busiest(3, bookings));
    }

    @Test(order = 6)
    void requestsGivenOutOfOrder() {
        int[][] bookings = {{9, 12}, {5, 6}, {0, 10}, {7, 8}, {2, 4}, {1, 3}};
        assertEquals(1, Studios.busiest(3, bookings));
    }

    @Test(order = 7)
    void delayedFinishesPastTheIntRange() {
        // two rooms, six long sessions: the delayed ones end near 2e9 and 3e9
        int[][] bookings = {
            {0, 1_000_000_000}, {1, 1_000_000_000}, {2, 1_000_000_000},
            {3, 1_000_000_000}, {4, 1_000_000_000}, {5, 1_000_000_000}};
        assertEquals(0, Studios.busiest(2, bookings)); // rooms 0 and 1 host three sessions each
    }

    @Test(order = 8, timeoutMs = 3500)
    void hundredThousandRequestsOnEightyThousandRooms() {
        int n = 100_000;
        int[][] bookings = new int[n][];
        for (int i = 0; i < n; i++) {
            bookings[i] = new int[] {i, i + 50_000 + (int) ((i * 7919L) % 100_000)};
        }
        Random random = new Random(5);
        for (int i = n - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            int[] swap = bookings[i];
            bookings[i] = bookings[j];
            bookings[j] = swap;
        }
        assertEquals(203, Studios.busiest(80_000, bookings));
        assertEquals(14, Studios.busiest(70_000, bookings)); // 351 rooms tie at 3 sessions
        assertEquals(4, Studios.busiest(60_000, bookings)); // 1,445 rooms tie at 3 sessions
    }
}
