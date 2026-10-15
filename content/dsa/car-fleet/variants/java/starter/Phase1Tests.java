import static kit.Check.assertEquals;

import kit.Test;

public class Phase1Tests {

    @Test(order = 1)
    void sample() {
        assertEquals(2, Convoy.groups(20, new int[] {2, 6, 9, 13}, new int[] {3, 2, 1, 4}));
    }

    @Test(order = 2)
    void emptyRoadAndSingleCar() {
        assertEquals(0, Convoy.groups(10, new int[] {}, new int[] {}));
        assertEquals(1, Convoy.groups(10, new int[] {3}, new int[] {5}));
    }

    @Test(order = 3)
    void nobodyCatchesUp() {
        assertEquals(3, Convoy.groups(12, new int[] {0, 4, 8}, new int[] {1, 2, 4}));
    }

    @Test(order = 4)
    void everyoneMerges() {
        assertEquals(1, Convoy.groups(12, new int[] {0, 4, 8}, new int[] {4, 2, 1}));
    }

    @Test(order = 5)
    void arrivingTogetherIsOneGroup() {
        assertEquals(1, Convoy.groups(10, new int[] {4, 6}, new int[] {3, 2}));
    }

    @Test(order = 6)
    void groupsFormInTwoWaves() {
        assertEquals(2, Convoy.groups(100, new int[] {0, 10, 50, 60, 90}, new int[] {10, 5, 2, 5, 1}));
    }

    @Test(order = 7)
    void inputOrderIsArbitrary() {
        assertEquals(2, Convoy.groups(20, new int[] {2, 13, 6, 9}, new int[] {3, 4, 2, 1}));
    }

    @Test(order = 8)
    void fractionalArrivalTimesStayApart() {
        // times 2.1 and 2.5: they differ, but both round down to 2
        assertEquals(2, Convoy.groups(30, new int[] {5, 9}, new int[] {10, 10}));
    }

    @Test(order = 9, timeoutMs = 2000)
    void largeNobodyCatchesUp() {
        int n = 100_000;
        int[] start = new int[n];
        for (int i = 0; i < n; i++) {
            start[i] = (int) ((i * 7_919L) % n); // 7,919 is coprime with n, so this lists every position once, out of order
        }
        int[] speed = new int[n];
        java.util.Arrays.fill(speed, 1);
        assertEquals(n, Convoy.groups(n, start, speed)); // equal speeds: nobody gains on anybody
    }

    @Test(order = 10, timeoutMs = 2000)
    void largeAllArriveTogether() {
        int n = 100_000;
        int[] start = new int[n];
        int[] speed = new int[n];
        for (int i = 0; i < n; i++) {
            start[i] = i;
            speed[i] = n - i; // every car needs exactly one time unit
        }
        assertEquals(1, Convoy.groups(n, start, speed));
    }
}
