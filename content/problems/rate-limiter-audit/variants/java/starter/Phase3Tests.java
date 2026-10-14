import static kit.Check.assertEquals;

import java.util.List;
import java.util.Map;
import kit.Test;

/**
 * A full day of traffic. Each test has a time limit; a correct but slow solution fails with TIMEOUT.
 * The two tests stress different parts of the code.
 */
public class Phase3Tests {

    /** One client with 500,000 requests. */
    @Test(order = 1, timeoutMs = 3000)
    void oneNoisyClient() {
        RateAudit audit = new RateAudit(Datasets.noisyClient("NOISY", 500_000, 5L));
        List<Long> rejected = audit.rejections("NOISY", 200, 1_000);
        long checksum = 0;
        for (long t : rejected) {
            checksum = checksum * 31 + t;
        }
        assertEquals(EXPECTED_NOISY_REJECTED, rejected.size(), "rejected count");
        assertEquals(EXPECTED_NOISY_CHECKSUM, checksum, "checksum over rejected times");
    }

    /** 40,000 clients with about eight requests each. */
    @Test(order = 2, timeoutMs = 3000)
    void manyQuietClients() {
        RateAudit audit = new RateAudit(Datasets.manyClients(40_000, 8, 9L));
        Map<String, Integer> counts = audit.rejectionCounts(3, 20_000);
        long total = 0;
        for (int n : counts.values()) {
            total += n;
        }
        assertEquals(EXPECTED_MANY_CLIENTS, counts.size(), "clients with rejections");
        assertEquals(EXPECTED_MANY_TOTAL, total, "total rejections");
    }

    static final int EXPECTED_NOISY_REJECTED = 349_879;
    static final long EXPECTED_NOISY_CHECKSUM = 977_855_894_575_460_586L;
    static final int EXPECTED_MANY_CLIENTS = 12_169;
    static final long EXPECTED_MANY_TOTAL = 18_213L;
}
