import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Remembers the values of keys over time. */
public class TimeMap {

    /** The history of one key: parallel lists, sorted by time because sets arrive in time order. */
    private static final class History {
        final List<Integer> times = new ArrayList<>();
        final List<String> values = new ArrayList<>();
    }

    private final Map<String, History> histories = new HashMap<>();

    /** Records that key took value at timestamp; timestamps per key only increase. */
    void set(String key, String value, int timestamp) {
        History history = histories.computeIfAbsent(key, k -> new History());
        history.times.add(timestamp);
        history.values.add(value);
    }

    /** Returns the value with the largest timestamp at or before the given one, or "" if none. */
    String get(String key, int timestamp) {
        History history = histories.get(key);
        if (history == null) {
            return "";
        }
        int low = 0;
        int high = history.times.size() - 1;
        int best = -1; // rightmost index whose time is at most the query
        while (low <= high) {
            int middle = (low + high) >>> 1;
            if (history.times.get(middle) <= timestamp) {
                best = middle;
                low = middle + 1;
            } else {
                high = middle - 1;
            }
        }
        return best < 0 ? "" : history.values.get(best);
    }
}
