import static kit.Check.assertEquals;

import java.util.List;
import java.util.Map;
import kit.Test;

/** Festival week: 200,000 parcels, 100,000 couriers. A correct but slow solution fails with TIMEOUT. */
public class Phase3Tests {

    private static long checksum(Plan plan) {
        long sum = 0;
        int i = 0;
        for (Map.Entry<String, Long> e : plan.loads().entrySet()) {
            sum += e.getValue() * (++i % 97);
        }
        return sum;
    }

    @Test(order = 1, timeoutMs = 5000)
    void festivalWeek() {
        Plan plan = new LoadBalancer(Datasets.couriers(100_000)).balance(Datasets.parcels(200_000, 3L));
        assertEquals(EXPECTED_HEAVIEST, plan.heaviestLoad(), "heaviest load");
        assertEquals(EXPECTED_CHECKSUM, checksum(plan), "checksum over loads");
    }

    @Test(order = 2, timeoutMs = 5000)
    void festivalWeekWithSmallVans() {
        Plan plan = new LoadBalancer(Datasets.couriers(100_000))
                .balanceWithCapacity(Datasets.parcels(200_000, 4L), 24_000);
        List<String> left = plan.leftOver();
        assertEquals(EXPECTED_LEFT_OVER, left.size(), "left over");
        assertEquals(EXPECTED_SMALL_CHECKSUM, checksum(plan), "checksum over loads");
    }

    static final long EXPECTED_HEAVIEST = 29_997L;
    static final long EXPECTED_CHECKSUM = 38_889_288_031L;
    static final int EXPECTED_LEFT_OVER = 4_742;
    static final long EXPECTED_SMALL_CHECKSUM = 32_630_062_532L;
}
