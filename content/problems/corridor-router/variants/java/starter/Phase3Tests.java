import static kit.Check.assertEquals;

import java.util.Optional;
import java.util.Random;
import kit.Test;

/**
 * Large inputs. Each test has a time limit; a correct but slow implementation fails with TIMEOUT.
 * Read how each test uses the router: the three workloads are different.
 */
public class Phase3Tests {

    /** 3,000 nodes, 45,000 corridors. 60 unrelated lookups, up to 7 hops each. */
    @Test(order = 1, timeoutMs = 5000)
    void manyUnrelatedLookups() {
        Network network = Datasets.randomNetwork(3_000, 45_000, 20261013L);
        RouteFinder finder = new RouteFinder(network);
        Random random = new Random(7);
        long checksum = 0;
        for (int i = 0; i < 60; i++) {
            String from = Datasets.node(random.nextInt(3_000));
            String to = Datasets.node(random.nextInt(3_000));
            Optional<Route> route = finder.cheapest(from, to, 7);
            checksum += route.map(r -> r.totalFeeMinor() * 31 + r.hops()).orElse(1L);
        }
        assertEquals(EXPECTED_UNRELATED, checksum, "checksum over 60 lookups");
    }

    /** A sparse network (2,000 nodes, 6,000 corridors) where routes may be long: up to 40 hops. */
    @Test(order = 2, timeoutMs = 4000)
    void longRoutesOnASparseNetwork() {
        Network network = Datasets.randomNetwork(2_000, 6_000, 5L);
        RouteFinder finder = new RouteFinder(network);
        Random random = new Random(3);
        long checksum = 0;
        for (int i = 0; i < 100; i++) {
            String from = Datasets.node(random.nextInt(2_000));
            String to = Datasets.node(random.nextInt(2_000));
            Optional<Route> route = finder.cheapest(from, to, 40);
            checksum += route.map(r -> r.totalFeeMinor() * 31 + r.hops()).orElse(1L);
        }
        assertEquals(EXPECTED_LONG, checksum, "checksum over 100 lookups");
    }

    /**
     * A settlement batch: 6,000 lookups that all start at the same node, on a network that does
     * not change while the batch runs. Afterwards a new corridor is added and must be honoured.
     */
    @Test(order = 3, timeoutMs = 3000)
    void settlementBatchFromOneOrigin() {
        Network network = Datasets.randomNetwork(3_000, 60_000, 99L);
        RouteFinder finder = new RouteFinder(network);
        String origin = Datasets.node(17);
        Random random = new Random(11);
        long checksum = 0;
        for (int i = 0; i < 6_000; i++) {
            String to = Datasets.node(random.nextInt(3_000));
            Optional<Route> route = finder.cheapest(origin, to, 7);
            checksum += route.map(r -> r.totalFeeMinor() * 31 + r.hops()).orElse(1L);
        }
        assertEquals(EXPECTED_BATCH, checksum, "checksum over 6,000 lookups");

        String target = Datasets.node(2_500);
        long before = finder.cheapest(origin, target, 7).orElseThrow().totalFeeMinor();
        network.add(origin, target, 1);
        long after = finder.cheapest(origin, target, 7).orElseThrow().totalFeeMinor();
        assertEquals(1, after, "fee after a 1-unit corridor was added (it was " + before + " before)");
    }

    static final long EXPECTED_UNRELATED = 12584285L;
    static final long EXPECTED_LONG = 87997971L;
    static final long EXPECTED_BATCH = 867124922L;
}
