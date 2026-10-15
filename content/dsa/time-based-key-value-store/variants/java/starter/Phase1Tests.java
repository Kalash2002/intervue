import static kit.Check.assertEquals;

import java.util.Random;
import kit.Test;

public class Phase1Tests {

    @Test(order = 1)
    void sampleHistory() {
        TimeMap map = new TimeMap();
        map.set("door", "locked", 10);
        map.set("door", "open", 20);
        assertEquals("locked", map.get("door", 15));
        assertEquals("open", map.get("door", 20));
        assertEquals("", map.get("door", 5));
    }

    @Test(order = 2)
    void unknownKey() {
        TimeMap map = new TimeMap();
        assertEquals("", map.get("window", 100));
        map.set("door", "locked", 10);
        assertEquals("", map.get("window", 100));
    }

    @Test(order = 3)
    void exactTimestampIsIncluded() {
        TimeMap map = new TimeMap();
        map.set("k", "a", 5);
        map.set("k", "b", 9);
        assertEquals("a", map.get("k", 5));
        assertEquals("b", map.get("k", 9));
    }

    @Test(order = 4)
    void queryBeyondTheLastRecord() {
        TimeMap map = new TimeMap();
        map.set("k", "a", 5);
        map.set("k", "b", 9);
        assertEquals("b", map.get("k", 1_000_000_000));
    }

    @Test(order = 5)
    void queryBeforeTheFirstRecord() {
        TimeMap map = new TimeMap();
        map.set("k", "a", 5);
        assertEquals("", map.get("k", 4));
        assertEquals("", map.get("k", 0));
    }

    @Test(order = 6)
    void keysAreIndependent() {
        TimeMap map = new TimeMap();
        map.set("a", "x", 1);
        map.set("b", "y", 2);
        map.set("a", "z", 3);
        assertEquals("x", map.get("a", 2));
        assertEquals("y", map.get("b", 50));
        assertEquals("z", map.get("a", 50));
        assertEquals("", map.get("b", 1));
    }

    @Test(order = 7)
    void recordAtTimestampZero() {
        TimeMap map = new TimeMap();
        map.set("k", "start", 0);
        assertEquals("start", map.get("k", 0));
        assertEquals("start", map.get("k", 7));
    }

    @Test(order = 8)
    void longHistoryBetweenRecords() {
        TimeMap map = new TimeMap();
        for (int t = 10; t <= 1000; t += 10) {
            map.set("k", "v" + t, t);
        }
        assertEquals("v10", map.get("k", 10));
        assertEquals("v10", map.get("k", 19));
        assertEquals("v500", map.get("k", 505));
        assertEquals("v1000", map.get("k", 1000));
        assertEquals("", map.get("k", 9));
    }

    @Test(order = 9, timeoutMs = 2000)
    void largeHistory() {
        int n = 300_000;
        TimeMap map = new TimeMap();
        for (int i = 0; i < n; i++) {
            map.set("sensor", "v" + i, 10 + i * 3); // record i is at time 10 + 3i
        }
        Random random = new Random(3);
        for (int q = 0; q < n; q++) {
            int time = random.nextInt(10 + 3 * n + 20);
            String want = time < 10 ? "" : "v" + Math.min((time - 10) / 3, n - 1);
            assertEquals(want, map.get("sensor", time));
        }
    }
}
