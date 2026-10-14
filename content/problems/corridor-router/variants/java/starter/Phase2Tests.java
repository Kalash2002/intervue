import static kit.Check.assertEquals;
import static kit.Check.assertThrows;
import static kit.Check.assertTrue;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import kit.Test;

public class Phase2Tests {

    /** Checks that a route really exists in the network and that its fee adds up. */
    static void assertValid(Network network, Route route, String from, String to) {
        List<String> stops = route.stops();
        assertEquals(from, stops.get(0), "first stop");
        assertEquals(to, stops.get(stops.size() - 1), "last stop");
        long fee = 0;
        for (int i = 0; i + 1 < stops.size(); i++) {
            String a = stops.get(i);
            String b = stops.get(i + 1);
            long best = Long.MAX_VALUE;
            for (Corridor c : network.corridorsFrom(a)) {
                if (c.active() && c.to().equals(b)) {
                    best = Math.min(best, c.feeMinor());
                }
            }
            assertTrue(best != Long.MAX_VALUE, "route uses " + a + " > " + b + " but no active corridor exists");
            fee += best;
        }
        assertEquals(fee, route.totalFeeMinor(), "fee of " + stops);
    }

    @Test(order = 1)
    void detourIsCheaperThanDirect() {
        Network network = new Network();
        network.add("A", "B", 100);
        network.add("A", "C", 20);
        network.add("C", "B", 30);
        Route route = new RouteFinder(network).cheapest("A", "B", 5).orElseThrow();
        assertEquals(List.of("A", "C", "B"), route.stops());
        assertEquals(50, route.totalFeeMinor());
    }

    @Test(order = 2)
    void hopLimitForcesThePricierRoute() {
        Network network = new Network();
        network.add("A", "B", 1);
        network.add("B", "C", 1);
        network.add("C", "D", 1);
        network.add("D", "E", 1);
        network.add("A", "X", 40);
        network.add("X", "E", 40);
        network.add("A", "E", 500);
        RouteFinder finder = new RouteFinder(network);
        assertEquals(4, finder.cheapest("A", "E", 4).orElseThrow().totalFeeMinor(), "maxHops 4");
        assertEquals(80, finder.cheapest("A", "E", 3).orElseThrow().totalFeeMinor(), "maxHops 3");
        assertEquals(80, finder.cheapest("A", "E", 2).orElseThrow().totalFeeMinor(), "maxHops 2");
        assertEquals(500, finder.cheapest("A", "E", 1).orElseThrow().totalFeeMinor(), "maxHops 1");
    }

    @Test(order = 3)
    void hopLimitCanMakeTheDestinationUnreachable() {
        Network network = new Network();
        network.add("A", "B", 1);
        network.add("B", "C", 1);
        RouteFinder finder = new RouteFinder(network);
        assertEquals(Optional.empty(), finder.cheapest("A", "C", 1));
        assertEquals(Optional.empty(), finder.cheapest("A", "C", 0));
        assertTrue(finder.cheapest("A", "C", 2).isPresent(), "two hops are enough");
    }

    @Test(order = 4)
    void equalFeePrefersFewerHops() {
        Network network = new Network();
        network.add("A", "B", 30);
        network.add("B", "C", 30);
        network.add("C", "D", 30);
        network.add("A", "D", 90);
        Route route = new RouteFinder(network).cheapest("A", "D", 5).orElseThrow();
        assertEquals(90, route.totalFeeMinor());
        assertEquals(1, route.hops(), "hops for " + route.stops());
    }

    @Test(order = 5)
    void inactiveCorridorsAreNotUsed() {
        Network network = new Network();
        network.add(new Corridor("A", "B", 1, false));
        network.add("A", "B", 75);
        Route route = new RouteFinder(network).cheapest("A", "B", 3).orElseThrow();
        assertEquals(75, route.totalFeeMinor());
    }

    @Test(order = 6)
    void cheapestOfParallelCorridorsIsUsed() {
        Network network = new Network();
        network.add("A", "B", 90);
        network.add("A", "B", 15);
        network.add("A", "B", 60);
        network.add("B", "C", 10);
        Route route = new RouteFinder(network).cheapest("A", "C", 3).orElseThrow();
        assertEquals(25, route.totalFeeMinor());
        assertValid(network, route, "A", "C");
    }

    @Test(order = 7)
    void routeToSelfIsFree() {
        Network network = new Network();
        network.add("A", "B", 10);
        network.add("B", "A", 10);
        Route route = new RouteFinder(network).cheapest("A", "A", 4).orElseThrow();
        assertEquals(List.of("A"), route.stops());
        assertEquals(0, route.totalFeeMinor());
        assertEquals(List.of("A"), new RouteFinder(network).cheapest("A", "A", 0).orElseThrow().stops());
    }

    @Test(order = 8)
    void freeLoopsDoNotTrapTheSearch() {
        Network network = new Network();
        network.add("A", "B", 0);
        network.add("B", "A", 0);
        network.add("B", "C", 0);
        network.add("C", "B", 0);
        network.add("C", "D", 7);
        Route route = new RouteFinder(network).cheapest("A", "D", 10).orElseThrow();
        assertEquals(7, route.totalFeeMinor());
        assertEquals(List.of("A", "B", "C", "D"), route.stops());
    }

    @Test(order = 9)
    void unknownNodesAndBadLimits() {
        Network network = new Network();
        network.add("A", "B", 10);
        RouteFinder finder = new RouteFinder(network);
        assertEquals(Optional.empty(), finder.cheapest("A", "Z", 3));
        assertEquals(Optional.empty(), finder.cheapest("Z", "A", 3));
        assertThrows(IllegalArgumentException.class, () -> finder.cheapest("A", "B", -1));
    }

    @Test(order = 10)
    void sampleNetwork() throws Exception {
        Network network = NetworkLoader.fromFile(Path.of("data/corridors.txt"));
        RouteFinder finder = new RouteFinder(network);
        Route toUsd = finder.cheapest("INR", "USD", 4).orElseThrow();
        assertEquals(65, toUsd.totalFeeMinor(), "INR to USD, up to 4 hops");
        assertValid(network, toUsd, "INR", "USD");
        assertEquals(70, finder.cheapest("INR", "USD", 2).orElseThrow().totalFeeMinor(), "INR to USD, up to 2 hops");
        assertEquals(120, finder.cheapest("INR", "USD", 1).orElseThrow().totalFeeMinor(), "INR to USD, 1 hop");
        Route toGbp = finder.cheapest("INR", "GBP", 6).orElseThrow();
        assertEquals(100, toGbp.totalFeeMinor(), "INR to GBP");
        assertValid(network, toGbp, "INR", "GBP");
    }
}
