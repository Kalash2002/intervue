import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** A key-value cache of fixed capacity that evicts the least recently used key when full. */
public class LRUCache {
    private final int capacity;
    private final Map<Integer, Integer> values = new HashMap<>();
    private final List<Integer> order = new ArrayList<>(); // least recently used first

    /** Makes an empty cache that holds at most capacity keys. */
    LRUCache(int capacity) {
        this.capacity = capacity;
    }

    /** Returns the value stored for key, or -1 when key is not in the cache; a hit makes key the most recently used. */
    int get(int key) {
        Integer value = values.get(key);
        if (value == null) {
            return -1;
        }
        order.remove(Integer.valueOf(key)); // a scan of the whole order list
        order.add(key);
        return value;
    }

    /** Stores value for key and makes key the most recently used, evicting the least recently used key if a new key does not fit. */
    void put(int key, int value) {
        if (values.containsKey(key)) {
            order.remove(Integer.valueOf(key));
        } else if (values.size() == capacity) {
            values.remove(order.remove(0));
        }
        values.put(key, value);
        order.add(key);
    }
}
