import static kit.Check.assertEquals;

import java.util.ArrayList;
import java.util.List;
import kit.Test;

public class Phase1Tests {

    private static final int[][] SAMPLE = {{0, 1, 100}, {1, 2, 100}, {2, 3, 100}, {0, 2, 250}, {1, 3, 300}};

    @Test(order = 1)
    void sampleWithOneStop() {
        assertEquals(350, Fares.cheapest(4, SAMPLE, 0, 3, 1));
    }

    @Test(order = 2)
    void sampleWithMoreAndFewerStops() {
        assertEquals(300, Fares.cheapest(4, SAMPLE, 0, 3, 2));
        assertEquals(300, Fares.cheapest(4, SAMPLE, 0, 3, 4));
        assertEquals(-1, Fares.cheapest(4, SAMPLE, 0, 3, 0));
    }

    @Test(order = 3)
    void directFlightAgainstATripWithAStop() {
        int[][] flights = {{0, 1, 50}, {0, 2, 10}, {2, 1, 10}};
        assertEquals(50, Fares.cheapest(3, flights, 0, 1, 0));
        assertEquals(20, Fares.cheapest(3, flights, 0, 1, 1));
    }

    @Test(order = 4)
    void cycleInTheNetwork() {
        int[][] flights = {{0, 1, 5}, {1, 0, 5}, {1, 2, 5}};
        assertEquals(10, Fares.cheapest(3, flights, 0, 2, 3));
    }

    @Test(order = 5)
    void unreachableDestination() {
        assertEquals(-1, Fares.cheapest(3, new int[][] {{0, 1, 5}}, 0, 2, 3));
        assertEquals(-1, Fares.cheapest(3, new int[][] {}, 0, 2, 3));
    }

    @Test(order = 6)
    void flightsOnlyGoOneWay() {
        assertEquals(-1, Fares.cheapest(2, new int[][] {{1, 0, 5}}, 0, 1, 2));
    }

    @Test(order = 7)
    void cheapestTripIgnoringTheLimitIsTooLong() {
        int[][] flights = {{0, 1, 1}, {1, 2, 1}, {2, 3, 1}, {0, 3, 10}};
        assertEquals(10, Fares.cheapest(4, flights, 0, 3, 1));
        assertEquals(3, Fares.cheapest(4, flights, 0, 3, 2));
    }

    @Test(order = 8)
    void flightsOfOneRoundMustNotChain() {
        int[][] flights = {{0, 1, 1}, {1, 2, 1}};
        assertEquals(-1, Fares.cheapest(3, flights, 0, 2, 0));
        assertEquals(2, Fares.cheapest(3, flights, 0, 2, 1));
    }

    @Test(order = 9, timeoutMs = 1000)
    void layeredNetworkWithManyTrips() {
        int layers = 15;
        int width = 4;
        int target = 1 + layers * width;
        List<int[]> list = new ArrayList<>();
        for (int j = 0; j < width; j++) {
            list.add(new int[] {0, 1 + j, 10 + j}); // from city 0 into the first layer
        }
        for (int layer = 1; layer < layers; layer++) {
            for (int a = 0; a < width; a++) {
                for (int b = 0; b < width; b++) {
                    int price = 10 + Math.abs(a - b); // staying in the same column is cheapest
                    list.add(new int[] {1 + (layer - 1) * width + a, 1 + layer * width + b, price});
                }
            }
        }
        for (int a = 0; a < width; a++) {
            list.add(new int[] {1 + (layers - 1) * width + a, target, 10});
        }
        int[][] flights = list.toArray(new int[0][]);
        int n = target + 1;
        // Every trip uses layers + 1 = 16 flights, so 15 stops; the cheapest keeps to column 0.
        assertEquals(10 * (layers + 1), Fares.cheapest(n, flights, 0, target, layers));
        assertEquals(-1, Fares.cheapest(n, flights, 0, target, layers - 1));
    }
}
