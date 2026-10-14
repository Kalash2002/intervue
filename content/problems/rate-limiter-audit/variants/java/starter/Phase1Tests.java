import static kit.Check.assertEquals;
import static kit.Check.assertFalse;
import static kit.Check.assertTrue;

import java.nio.file.Path;
import java.util.List;
import kit.Test;

public class Phase1Tests {

    @Test(order = 1)
    void windowExcludesItsStartAndIncludesItsEnd() {
        RequestLog log = new RequestLog();
        log.add(new Request("a", 100));
        log.add(new Request("a", 200));
        log.add(new Request("a", 1100));
        RateAudit audit = new RateAudit(log);
        assertEquals(2, audit.countInWindow("a", 1100, 1000));
        assertEquals(3, audit.countInWindow("a", 1100, 1001));
    }

    @Test(order = 2)
    void otherClientsAreIgnored() {
        RequestLog log = new RequestLog();
        log.add(new Request("a", 10));
        log.add(new Request("b", 20));
        RateAudit audit = new RateAudit(log);
        assertEquals(1, audit.countInWindow("a", 50, 100));
        assertEquals(0, audit.countInWindow("nobody", 50, 100));
        assertFalse(audit.isOverLimit("a", 50, 1, 100), "one request is not over a limit of one");
    }

    @Test(order = 3)
    void parsesWholeSecondsAndShortDecimals() {
        assertEquals(12_000, LogParser.parseMillis("12"));
        assertEquals(7_500, LogParser.parseMillis("7.5"));
        assertEquals(120, LogParser.parseMillis("0.120"));
    }

    @Test(order = 4)
    void parsesEveryMillisecondExactly() {
        assertEquals(1_003, LogParser.parseMillis("1.003"));
        assertEquals(2_006, LogParser.parseMillis("2.006"));
        assertEquals(4_012, LogParser.parseMillis("4.012"));
    }

    @Test(order = 5)
    void sampleFileTimes() throws Exception {
        RequestLog log = LogParser.fromFile(Path.of("data/requests.csv"));
        assertEquals(List.of(120L, 480L, 1_003L, 1_250L, 1_900L, 2_006L, 2_010L, 2_900L), log.forClient("alpha"));
    }

    @Test(order = 6)
    void sampleFileWindowNearABoundary() throws Exception {
        RateAudit audit = new RateAudit(LogParser.fromFile(Path.of("data/requests.csv")));
        assertEquals(2, audit.countInWindow("alpha", 2_005, 1_000));
        assertTrue(audit.isOverLimit("alpha", 2_010, 3, 1_000), "four requests in the second before 2.010");
    }
}
