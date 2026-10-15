import static kit.Check.assertEquals;

import java.util.Random;
import kit.Test;

public class Phase1Tests {

    @Test(order = 1)
    void sample() {
        assertEquals(2, Rooms.minRooms(new int[][] {{9, 11}, {10, 12}, {11, 13}}));
    }

    @Test(order = 2)
    void emptyList() {
        assertEquals(0, Rooms.minRooms(new int[][] {}));
    }

    @Test(order = 3)
    void singleBooking() {
        assertEquals(1, Rooms.minRooms(new int[][] {{4, 9}}));
    }

    @Test(order = 4)
    void backToBackShareARoom() {
        assertEquals(1, Rooms.minRooms(new int[][] {{0, 5}, {5, 10}, {10, 15}}));
        assertEquals(1, Rooms.minRooms(new int[][] {{1, 2}, {3, 4}}));
    }

    @Test(order = 5)
    void allActiveTogether() {
        assertEquals(3, Rooms.minRooms(new int[][] {{5, 9}, {6, 8}, {7, 10}}));
        assertEquals(3, Rooms.minRooms(new int[][] {{2, 4}, {2, 4}, {2, 4}}));
    }

    @Test(order = 6)
    void longBookingWithShortOnesInsideIt() {
        assertEquals(2, Rooms.minRooms(new int[][] {{1, 10}, {2, 3}, {4, 5}, {6, 7}}));
    }

    @Test(order = 7)
    void staggeredChain() {
        assertEquals(3, Rooms.minRooms(new int[][] {{0, 3}, {1, 4}, {2, 5}, {3, 6}}));
    }

    @Test(order = 8)
    void unsortedBookings() {
        assertEquals(2, Rooms.minRooms(new int[][] {{6, 8}, {1, 3}, {2, 7}, {3, 6}}));
    }

    @Test(order = 9, timeoutMs = 1000)
    void sixtyThousandShuffledBookings() {
        int n = 60_000;
        int[][] bookings = new int[n][];
        for (int i = 0; i < n; i++) {
            bookings[i] = new int[] {i, i + 1000}; // 1,000 are active at any minute from 999 on
        }
        Random random = new Random(21);
        for (int i = n - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            int[] swap = bookings[i];
            bookings[i] = bookings[j];
            bookings[j] = swap;
        }
        assertEquals(1000, Rooms.minRooms(bookings));
    }
}
