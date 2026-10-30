import static kit.Check.assertEquals;

import java.util.Arrays;
import java.util.Random;
import kit.Test;

public class Phase1Tests {

    private static void expect(boolean want, int[][] bookings) {
        String input = bookings.length <= 20 ? "bookings = " + Arrays.deepToString(bookings) : bookings.length + " bookings";
        assertEquals(want, Rooms.fitInOneRoom(bookings), input);
    }

    @Test(order = 1)
    void sample() {
        expect(false, new int[][] {{30, 90}, {100, 120}, {40, 50}});
        expect(true, new int[][] {{14, 16}, {9, 11}, {16, 18}, {11, 13}});
        expect(false, new int[][] {{45, 60}, {45, 46}});
    }

    @Test(order = 2)
    void touchingBookingsShareTheRoom() {
        expect(true, new int[][] {{30, 45}, {45, 60}});
        expect(true, new int[][] {{60, 75}, {30, 45}, {45, 60}});
    }

    @Test(order = 3)
    void anOverlapOfOneMinute() {
        expect(false, new int[][] {{30, 46}, {45, 60}});
        expect(false, new int[][] {{45, 60}, {30, 46}});
    }

    @Test(order = 4)
    void aBookingInsideAnother() {
        expect(false, new int[][] {{200, 300}, {250, 251}});
        expect(false, new int[][] {{250, 251}, {200, 300}});
    }

    @Test(order = 5)
    void aClashBetweenBookingsFarApartInTheList() {
        // [20,22] and [21,25] clash; no two neighbours in the given order do
        expect(false, new int[][] {{5, 7}, {20, 22}, {1, 3}, {8, 9}, {21, 25}, {12, 14}});
    }

    @Test(order = 6)
    void noBookingsOrOne() {
        expect(true, new int[][] {});
        expect(true, new int[][] {{7, 8}});
        expect(true, new int[][] {{0, 1_000_000_000}});
    }

    @Test(order = 7)
    void identicalBookings() {
        expect(false, new int[][] {{3, 9}, {3, 9}});
    }

    @Test(order = 8)
    void randomShortLists() {
        Random random = new Random(1561);
        for (int round = 0; round < 300; round++) {
            int n = random.nextInt(9);
            int[][] bookings = new int[n][];
            int at = 100;
            for (int i = 0; i < n; i++) {
                if (round % 2 == 0) { // one after another, with gaps of 0 to 2 minutes
                    at += random.nextInt(3);
                    bookings[i] = new int[] {at, at + 1 + random.nextInt(4)};
                    at = bookings[i][1];
                } else {
                    int start = 100 + random.nextInt(30);
                    bookings[i] = new int[] {start, start + 1 + random.nextInt(4)};
                }
            }
            for (int i = n - 1; i > 0; i--) {
                int j = random.nextInt(i + 1);
                int[] swap = bookings[i];
                bookings[i] = bookings[j];
                bookings[j] = swap;
            }
            expect(noTwoClash(bookings), bookings);
        }
    }

    /** Compares every pair of bookings. */
    private static boolean noTwoClash(int[][] bookings) {
        for (int i = 0; i < bookings.length; i++) {
            for (int j = i + 1; j < bookings.length; j++) {
                if (bookings[i][0] < bookings[j][1] && bookings[j][0] < bookings[i][1]) {
                    return false;
                }
            }
        }
        return true;
    }

    @Test(order = 9, timeoutMs = 400)
    void aHundredThousandTouchingBookings() {
        // Booking i is [2i, 2i + 2), so each one ends as the next starts. The even ones are listed
        // first, in order, then the odd ones from the last back.
        int n = 100_000;
        int[][] bookings = new int[n][];
        int next = 0;
        for (int i = 0; i < n; i += 2) {
            bookings[next++] = new int[] {2 * i, 2 * i + 2};
        }
        for (int i = n - 1; i > 0; i -= 2) {
            bookings[next++] = new int[] {2 * i, 2 * i + 2};
        }
        expect(true, bookings);
    }

    @Test(order = 10, timeoutMs = 400)
    void oneClashInTheMiddle() {
        // Booking i is [10i, 10i + 5), in order, except that booking 70,000 is moved inside
        // booking 30,000: [300,002, 300,004). That is the only clash, between positions 30,000
        // and 70,000 of the list.
        int n = 100_000;
        int[][] bookings = new int[n][];
        for (int i = 0; i < n; i++) {
            bookings[i] = new int[] {10 * i, 10 * i + 5};
        }
        bookings[70_000] = new int[] {300_002, 300_004};
        expect(false, bookings);
    }
}
