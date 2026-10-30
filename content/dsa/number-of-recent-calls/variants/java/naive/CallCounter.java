import java.util.ArrayList;
import java.util.List;

/** Counts the calls that arrived within a recent window of milliseconds. Keeps every call and walks back. */
public class CallCounter {
    private final int window;
    private final List<Integer> times = new ArrayList<>();

    CallCounter(int window) {
        this.window = window;
    }

    int ping(int t) {
        times.add(t);
        int count = 0;
        // walk back through the history until a call is too old
        for (int i = times.size() - 1; i >= 0 && times.get(i) >= t - window; i--) {
            count++;
        }
        return count;
    }
}
