import static kit.Check.assertEquals;
import static kit.Check.assertThrows;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import kit.Test;

public class Phase2Tests {

    private static RateAudit audit(String client, long... times) {
        RequestLog log = new RequestLog();
        for (long t : times) {
            log.add(new Request(client, t));
        }
        return new RateAudit(log);
    }

    @Test(order = 1)
    void requestsOverTheLimitAreRejected() {
        assertEquals(List.of(30L), audit("a", 10, 20, 30).rejections("a", 2, 100));
    }

    @Test(order = 2)
    void rejectedRequestsDoNotCount() {
        // 50 is rejected; if it counted, 120 would be rejected too.
        assertEquals(List.of(50L), audit("a", 0, 50, 120).rejections("a", 1, 100));
    }

    @Test(order = 3)
    void theWindowStartIsExcluded() {
        assertEquals(List.of(), audit("a", 0, 100, 200).rejections("a", 1, 100));
        assertEquals(List.of(100L), audit("a", 0, 100, 200).rejections("a", 1, 101));
    }

    @Test(order = 4)
    void requestsAtTheSameMillisecondEachCount() {
        assertEquals(List.of(5L, 5L), audit("a", 5, 5, 5, 5).rejections("a", 2, 10));
    }

    @Test(order = 5)
    void badSettingsAreRefused() {
        RateAudit a = audit("a", 1);
        assertThrows(IllegalArgumentException.class, () -> a.rejections("a", 0, 100));
        assertThrows(IllegalArgumentException.class, () -> a.rejections("a", 1, 0));
        assertThrows(IllegalArgumentException.class, () -> a.rejectionCounts(0, 100));
    }

    @Test(order = 6)
    void sampleFileRejections() throws Exception {
        RateAudit a = new RateAudit(LogParser.fromFile(Path.of("data/requests.csv")));
        assertEquals(List.of(1_003L, 2_006L, 2_010L), a.rejections("alpha", 2, 1_000));
        assertEquals(List.of(1_950L), a.rejections("beta", 1, 1_000));
        assertEquals(List.of(), a.rejections("nobody", 1, 1_000));
    }

    @Test(order = 7)
    void sampleFileCountsLeaveOutCleanClients() throws Exception {
        RateAudit a = new RateAudit(LogParser.fromFile(Path.of("data/requests.csv")));
        assertEquals(Map.of("alpha", 3), a.rejectionCounts(2, 1_000));
        assertEquals(List.of("alpha", "beta"), List.copyOf(a.rejectionCounts(1, 1_000).keySet()));
    }
}
