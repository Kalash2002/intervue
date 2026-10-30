import java.util.Arrays;

/** Assigns booking requests to numbered studio rooms. */
public class Studios {

    /** Returns the lowest-numbered room that hosted the most sessions; each booking is {start, end}. */
    static int busiest(int rooms, int[][] bookings) {
        int[][] byStart = bookings.clone();
        Arrays.sort(byStart, (a, b) -> Integer.compare(a[0], b[0]));
        long[] freeAt = new long[rooms];
        int[] hosted = new int[rooms];
        for (int[] booking : byStart) {
            int room = -1;
            int earliest = 0; // the room that frees first, lowest number on ties
            for (int i = 0; i < rooms; i++) {
                if (freeAt[i] <= booking[0]) {
                    room = i;
                    break;
                }
                if (freeAt[i] < freeAt[earliest]) {
                    earliest = i;
                }
            }
            if (room >= 0) {
                freeAt[room] = booking[1];
            } else {
                room = earliest;
                freeAt[room] += booking[1] - booking[0];
            }
            hosted[room]++;
        }
        int best = 0;
        for (int room = 1; room < rooms; room++) {
            if (hosted[room] > hosted[best]) {
                best = room;
            }
        }
        return best;
    }
}
