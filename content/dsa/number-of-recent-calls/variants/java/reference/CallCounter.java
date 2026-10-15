import java.util.ArrayDeque;
import java.util.Deque;

/** Counts the calls that arrived within a recent window of milliseconds. */
public class CallCounter {
    private final int window;
    private final Deque<Integer> recent = new ArrayDeque<>(); // arrival times, oldest first

    CallCounter(int window) {
        this.window = window;
    }

    int ping(int t) {
        recent.addLast(t);
        while (recent.peekFirst() < t - window) {
            recent.pollFirst(); // too old now, and too old for every later call
        }
        return recent.size();
    }
}
