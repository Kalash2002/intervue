import java.util.ArrayDeque;
import java.util.Deque;

/** Counts the pings received within the most recent window of seconds. */
public class Pings {

    private final int window;
    private final Deque<Integer> times = new ArrayDeque<>();

    /** Creates an empty counter; a ping at time t is recent at now when now - window < t <= now. */
    Pings(int window) {
        this.window = window;
    }

    /** Records one ping; calls come in non-decreasing time order. */
    void ping(int second) {
        drop(second);
        times.addLast(second);
    }

    /** Returns how many recorded pings are recent at the given second. */
    int recent(int second) {
        drop(second);
        return times.size();
    }

    private void drop(int second) {
        while (!times.isEmpty() && times.peekFirst() <= second - window) {
            times.removeFirst();
        }
    }
}
