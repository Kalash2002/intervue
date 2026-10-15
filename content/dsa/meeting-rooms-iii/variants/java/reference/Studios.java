import java.util.Arrays;
import java.util.PriorityQueue;

/** Assigns booking requests to numbered studio rooms. */
public class Studios {

    /** Returns the lowest-numbered room that hosted the most sessions; each booking is {start, end}. */
    static int busiest(int rooms, int[][] bookings) {
        int[][] byStart = bookings.clone();
        Arrays.sort(byStart, (a, b) -> Integer.compare(a[0], b[0]));
        PriorityQueue<Integer> free = new PriorityQueue<>();
        for (int room = 0; room < rooms; room++) {
            free.add(room);
        }
        PriorityQueue<long[]> busy = new PriorityQueue<>(
                (a, b) -> a[0] != b[0] ? Long.compare(a[0], b[0]) : Long.compare(a[1], b[1]));
        int[] hosted = new int[rooms];
        for (int[] booking : byStart) {
            while (!busy.isEmpty() && busy.peek()[0] <= booking[0]) {
                free.add((int) busy.poll()[1]);
            }
            int room;
            long finish;
            if (!free.isEmpty()) {
                room = free.poll();
                finish = booking[1];
            } else {
                long[] first = busy.poll();
                room = (int) first[1];
                finish = first[0] + (booking[1] - booking[0]);
            }
            hosted[room]++;
            busy.add(new long[] {finish, room});
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
