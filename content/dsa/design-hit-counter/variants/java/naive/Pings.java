import java.util.ArrayList;
import java.util.List;

/** Counts the pings received within the most recent window of seconds. */
public class Pings {

    private final int window;
    private final List<Integer> times = new ArrayList<>();

    /** Creates an empty counter; a ping at time t is recent at now when now - window < t <= now. */
    Pings(int window) {
        this.window = window;
    }

    /** Records one ping; calls come in non-decreasing time order. */
    void ping(int second) {
        times.add(second);
    }

    /** Returns how many recorded pings are recent at the given second. */
    int recent(int second) {
        int count = 0;
        for (int time : times) {
            if (time > second - window && time <= second) {
                count++;
            }
        }
        return count;
    }
}
