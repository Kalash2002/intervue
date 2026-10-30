import java.util.HashMap;
import java.util.Map;

/** A key-value cache of fixed capacity that evicts the least recently used key when full. */
public class LRUCache {

    private static final class Entry {
        final int key;
        int value;
        Entry prev;
        Entry next;

        Entry(int key, int value) {
            this.key = key;
            this.value = value;
        }
    }

    private final int capacity;
    private final Map<Integer, Entry> byKey = new HashMap<>();
    // sentinels: the most recently used entry follows head, the least recently used precedes tail
    private final Entry head = new Entry(0, 0);
    private final Entry tail = new Entry(0, 0);

    /** Makes an empty cache that holds at most capacity keys. */
    LRUCache(int capacity) {
        this.capacity = capacity;
        head.next = tail;
        tail.prev = head;
    }

    /** Returns the value stored for key, or -1 when key is not in the cache; a hit makes key the most recently used. */
    int get(int key) {
        Entry entry = byKey.get(key);
        if (entry == null) {
            return -1;
        }
        unlink(entry);
        pushFront(entry);
        return entry.value;
    }

    /** Stores value for key and makes key the most recently used, evicting the least recently used key if a new key does not fit. */
    void put(int key, int value) {
        Entry entry = byKey.get(key);
        if (entry != null) {
            entry.value = value; // an update never evicts
            unlink(entry);
            pushFront(entry);
            return;
        }
        if (byKey.size() == capacity) {
            Entry oldest = tail.prev;
            unlink(oldest);
            byKey.remove(oldest.key); // the entry keeps its key so the map can drop it
        }
        entry = new Entry(key, value);
        byKey.put(key, entry);
        pushFront(entry);
    }

    private void unlink(Entry entry) {
        entry.prev.next = entry.next;
        entry.next.prev = entry.prev;
    }

    private void pushFront(Entry entry) {
        entry.prev = head;
        entry.next = head.next;
        head.next.prev = entry;
        head.next = entry;
    }
}
