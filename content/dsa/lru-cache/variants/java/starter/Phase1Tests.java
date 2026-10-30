import static kit.Check.assertEquals;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import kit.Test;

public class Phase1Tests {

    @Test(order = 1)
    void sample() {
        LRUCache cache = new LRUCache(2);
        cache.put(5, 50);
        cache.put(8, 80);
        assertEquals(80, cache.get(8));
        assertEquals(50, cache.get(5)); // 8 is now the least recently used
        cache.put(3, 30);
        assertEquals(-1, cache.get(8));
        assertEquals(30, cache.get(3));
    }

    @Test(order = 2)
    void updateRefreshesTheKey() {
        LRUCache cache = new LRUCache(2);
        cache.put(1, 10);
        cache.put(2, 20);
        cache.put(1, 15); // 2 is now the least recently used
        cache.put(4, 40);
        assertEquals(15, cache.get(1));
        assertEquals(-1, cache.get(2));
        assertEquals(40, cache.get(4));
    }

    @Test(order = 3)
    void updateWhenFullEvictsNothing() {
        LRUCache cache = new LRUCache(2);
        cache.put(6, 60);
        cache.put(2, 20);
        cache.put(2, 25);
        assertEquals(60, cache.get(6));
        assertEquals(25, cache.get(2));
    }

    @Test(order = 4)
    void capacityOne() {
        LRUCache cache = new LRUCache(1);
        cache.put(7, 70);
        assertEquals(70, cache.get(7));
        cache.put(9, 90);
        assertEquals(-1, cache.get(7));
        assertEquals(90, cache.get(9));
    }

    @Test(order = 5)
    void getRefreshesTheKey() {
        LRUCache cache = new LRUCache(3);
        cache.put(4, 400);
        cache.put(6, 600);
        cache.put(9, 900);
        assertEquals(400, cache.get(4)); // 6 is now the least recently used
        cache.put(2, 200);
        assertEquals(-1, cache.get(6));
        assertEquals(400, cache.get(4));
        assertEquals(900, cache.get(9));
        assertEquals(200, cache.get(2));
    }

    @Test(order = 6)
    void missesAndZeroKeysAndValues() {
        LRUCache cache = new LRUCache(2);
        assertEquals(-1, cache.get(0));
        cache.put(0, 0);
        assertEquals(0, cache.get(0));
        cache.put(1_000_000, 1_000_000_000);
        assertEquals(1_000_000_000, cache.get(1_000_000));
    }

    @Test(order = 7)
    void aMissChangesNothing() {
        LRUCache cache = new LRUCache(2);
        cache.put(3, 31);
        cache.put(5, 52);
        assertEquals(-1, cache.get(7));
        cache.put(8, 83); // 3 is still the least recently used
        assertEquals(-1, cache.get(3));
        assertEquals(52, cache.get(5));
        assertEquals(83, cache.get(8));
    }

    @Test(order = 8)
    void twoCachesAreIndependent() {
        LRUCache small = new LRUCache(1);
        LRUCache large = new LRUCache(3);
        small.put(1, 11);
        large.put(1, 99);
        small.put(2, 22);
        assertEquals(99, large.get(1));
        assertEquals(-1, small.get(1));
        assertEquals(22, small.get(2));
    }

    @Test(order = 9, timeoutMs = 4000)
    void largeRun() {
        int capacity = 200_000;
        LRUCache cache = new LRUCache(capacity);
        // the expected answers come from the JDK's access-ordered LinkedHashMap
        Map<Integer, Integer> model = new LinkedHashMap<>(16, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<Integer, Integer> eldest) {
                return size() > capacity;
            }
        };
        for (int key = 0; key < capacity; key++) {
            cache.put(key, key);
            model.put(key, key);
        }
        Random random = new Random(42);
        for (int call = 0; call < 400_000; call++) {
            int key = random.nextInt(capacity + capacity / 4); // most keys are in the cache
            if (random.nextBoolean()) {
                int want = model.getOrDefault(key, -1);
                int got = cache.get(key);
                if (got != want) {
                    assertEquals(want, got, "get(" + key + ") at call " + call);
                }
            } else {
                int value = random.nextInt(1_000_000_001);
                cache.put(key, value);
                model.put(key, value);
            }
        }
    }
}
