import static kit.Check.assertEquals;

import kit.Test;

public class Phase1Tests {

    @Test(order = 1)
    void sample() {
        Pings pings = new Pings(5);
        pings.ping(1);
        pings.ping(2);
        pings.ping(2);
        pings.ping(4);
        assertEquals(4, pings.recent(4));
        pings.ping(6);
        assertEquals(4, pings.recent(6)); // the ping at 1 is 5 seconds old and out
        assertEquals(2, pings.recent(7)); // 4 and 6 remain
        assertEquals(1, pings.recent(10)); // only 6 is later than 5
        assertEquals(0, pings.recent(11));
    }

    @Test(order = 2)
    void noPings() {
        Pings pings = new Pings(10);
        assertEquals(0, pings.recent(100));
    }

    @Test(order = 3)
    void windowOfOne() {
        Pings pings = new Pings(1);
        pings.ping(3);
        pings.ping(3);
        assertEquals(2, pings.recent(3));
        assertEquals(0, pings.recent(4));
    }

    @Test(order = 4)
    void firstPingAtSecondZero() {
        Pings pings = new Pings(3);
        pings.ping(0);
        assertEquals(1, pings.recent(2));
        assertEquals(0, pings.recent(3)); // exactly 3 seconds old is out
    }

    @Test(order = 5)
    void manyPingsInOneSecond() {
        Pings pings = new Pings(10);
        for (int i = 0; i < 1000; i++) {
            pings.ping(5);
        }
        assertEquals(1000, pings.recent(14));
        assertEquals(0, pings.recent(15));
    }

    @Test(order = 6)
    void repeatedQueriesAtOneSecond() {
        Pings pings = new Pings(4);
        pings.ping(1);
        pings.ping(3);
        assertEquals(2, pings.recent(4));
        assertEquals(2, pings.recent(4));
        assertEquals(1, pings.recent(5)); // 1 is 4 seconds old and out, 3 remains
        assertEquals(1, pings.recent(5));
    }

    @Test(order = 7)
    void pingsThatArriveAfterTheOldOnesExpired() {
        Pings pings = new Pings(2);
        pings.ping(1);
        pings.ping(10);
        assertEquals(1, pings.recent(10));
        pings.ping(11);
        assertEquals(2, pings.recent(11));
        assertEquals(1, pings.recent(12));
    }

    @Test(order = 8, timeoutMs = 250)
    void hundredThousandPingsWithAQueryAfterEach() {
        Pings pings = new Pings(1000);
        long sum = 0;
        for (int i = 0; i < 100_000; i++) {
            pings.ping(i);
            sum += pings.recent(i); // min(i + 1, 1000)
        }
        assertEquals(99_500_500L, sum);
    }
}
