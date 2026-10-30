import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Remembers the values of keys over time. */
public class TimeMap {

    private final Map<String, List<Integer>> times = new HashMap<>();
    private final Map<String, List<String>> values = new HashMap<>();

    /** Records that key took value at timestamp; timestamps per key only increase. */
    void set(String key, String value, int timestamp) {
        times.computeIfAbsent(key, k -> new ArrayList<>()).add(timestamp);
        values.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
    }

    /** Returns the value with the largest timestamp at or before the given one, or "" if none. */
    String get(String key, int timestamp) {
        List<Integer> stamps = times.get(key);
        if (stamps == null) {
            return "";
        }
        String found = "";
        for (int i = 0; i < stamps.size(); i++) { // look at every record from the oldest
            if (stamps.get(i) <= timestamp) {
                found = values.get(key).get(i);
            }
        }
        return found;
    }
}
