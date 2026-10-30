import java.util.Comparator;
import java.util.PriorityQueue;

/** Hands arriving jobs to machines by weight. Two heaps: free machines, and busy ones by finishing time. */
public class Dispatcher {

    static int[] assign(int[] weights, int[] durations) {
        int machines = weights.length;
        PriorityQueue<Integer> free = new PriorityQueue<>(
                Comparator.<Integer>comparingInt(m -> weights[m]).thenComparingInt(m -> m));
        for (int m = 0; m < machines; m++) {
            free.add(m);
        }
        long[] freeAt = new long[machines]; // one machine queueing many jobs passes the int range
        PriorityQueue<Integer> busy = new PriorityQueue<>(
                Comparator.<Integer>comparingLong(m -> freeAt[m]).thenComparingInt(m -> m));
        int[] answer = new int[durations.length];
        long now = 0;
        for (int job = 0; job < durations.length; job++) {
            now = Math.max(now, job); // a job starts neither before it arrives nor before the job ahead of it
            release(busy, free, freeAt, now);
            if (free.isEmpty()) {
                now = freeAt[busy.peek()]; // wait for the first machine to finish
                release(busy, free, freeAt, now);
            }
            int machine = free.poll();
            answer[job] = machine;
            freeAt[machine] = now + durations[job];
            busy.add(machine);
        }
        return answer;
    }

    /** Moves every busy machine that has finished by the given second into the free queue. */
    private static void release(PriorityQueue<Integer> busy, PriorityQueue<Integer> free, long[] freeAt, long now) {
        while (!busy.isEmpty() && freeAt[busy.peek()] <= now) {
            free.add(busy.poll());
        }
    }
}
