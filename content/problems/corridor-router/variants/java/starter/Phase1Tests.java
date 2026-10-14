import static kit.Check.assertEquals;
import static kit.Check.assertTrue;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import kit.Test;

public class Phase1Tests {

    @Test(order = 1)
    void directCorridor() {
        Network network = new Network();
        network.add("A", "B", 50);
        Route route = new RouteFinder(network).fewestHops("A", "B").orElseThrow();
        assertEquals(List.of("A", "B"), route.stops());
        assertEquals(50, route.totalFeeMinor());
    }

    @Test(order = 2)
    void twoHopsWhenNoDirectCorridor() {
        Network network = new Network();
        network.add("A", "B", 50);
        network.add("B", "C", 70);
        Route route = new RouteFinder(network).fewestHops("A", "C").orElseThrow();
        assertEquals(List.of("A", "B", "C"), route.stops());
        assertEquals(120, route.totalFeeMinor());
    }

    @Test(order = 3)
    void noRouteAgainstTheDirectionOfACorridor() {
        Network network = new Network();
        network.add("A", "B", 50);
        assertEquals(Optional.empty(), new RouteFinder(network).fewestHops("B", "A"));
    }

    @Test(order = 4)
    void inactiveCorridorsAreNotUsed() {
        Network network = new Network();
        network.add(new Corridor("A", "C", 10, false));
        network.add("A", "B", 50);
        network.add("B", "C", 70);
        Route route = new RouteFinder(network).fewestHops("A", "C").orElseThrow();
        assertEquals(List.of("A", "B", "C"), route.stops());
    }

    @Test(order = 5)
    void routeToSelfHasOneStopAndNoFee() {
        Network network = new Network();
        network.add("A", "B", 50);
        Route route = new RouteFinder(network).fewestHops("A", "A").orElseThrow();
        assertEquals(List.of("A"), route.stops());
        assertEquals(0, route.totalFeeMinor());
    }

    @Test(order = 6)
    void unknownNodeHasNoRoute() {
        Network network = new Network();
        network.add("A", "B", 50);
        assertEquals(Optional.empty(), new RouteFinder(network).fewestHops("A", "Z"));
    }

    @Test(order = 7)
    void directCorridorBeatsADetour() {
        Network network = new Network();
        network.add("A", "C", 5);
        network.add("A", "B", 50);
        network.add("C", "B", 5);
        Route route = new RouteFinder(network).fewestHops("A", "B").orElseThrow();
        assertEquals(1, route.hops(), "hops for " + route.stops());
        assertEquals(50, route.totalFeeMinor());
    }

    @Test(order = 8)
    void sampleNetworkInrToUsdIsOneHop() throws Exception {
        Network network = NetworkLoader.fromFile(Path.of("data/corridors.txt"));
        Route route = new RouteFinder(network).fewestHops("INR", "USD").orElseThrow();
        assertEquals(List.of("INR", "USD"), route.stops());
        assertEquals(120, route.totalFeeMinor());
    }

    @Test(order = 9)
    void sampleNetworkInrToBrlIsTwoHops() throws Exception {
        Network network = NetworkLoader.fromFile(Path.of("data/corridors.txt"));
        Route route = new RouteFinder(network).fewestHops("INR", "BRL").orElseThrow();
        assertEquals(2, route.hops(), "hops for " + route.stops());
        assertTrue(route.stops().equals(List.of("INR", "USD", "BRL")), "expected INR > USD > BRL but was " + route.stops());
    }
}
