import static kit.Check.assertEquals;

import java.util.List;
import kit.Test;

/** Sale day: 400,000 orders. Each test has a time limit; a correct but slow solution fails with TIMEOUT. */
public class Phase3Tests {

    @Test(order = 1, timeoutMs = 4000)
    void suspectedOnSaleDay() {
        DuplicateDetector d = new DuplicateDetector(Datasets.saleDay(400_000, 11L));
        List<String> suspected = d.suspectedDuplicates(30);
        long checksum = 0;
        for (String id : suspected) {
            checksum = checksum * 31 + Long.parseLong(id.substring(1));
        }
        assertEquals(EXPECTED_SUSPECTED, suspected.size(), "suspected count");
        assertEquals(EXPECTED_CHECKSUM, checksum, "checksum over suspected ids");
    }

    @Test(order = 2, timeoutMs = 4000)
    void worstSkusOnSaleDay() {
        DuplicateDetector d = new DuplicateDetector(Datasets.saleDay(400_000, 13L));
        assertEquals(EXPECTED_TOP, d.mostDuplicatedSkus(3));
    }

    static final int EXPECTED_SUSPECTED = 606;
    static final long EXPECTED_CHECKSUM = -2151620993961751798L;
    static final List<SkuCount> EXPECTED_TOP =
            List.of(new SkuCount("SKU-33", 246), new SkuCount("SKU-3", 243), new SkuCount("SKU-35", 243));
}
