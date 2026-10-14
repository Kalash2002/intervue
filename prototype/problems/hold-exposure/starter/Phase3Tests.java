import static kit.Check.assertEquals;

import java.util.Map;
import java.util.OptionalInt;
import kit.Test;

/**
 * Large inputs. Each test has a time limit; a correct but slow implementation fails with TIMEOUT.
 * The two tests stress different parts of the code.
 */
public class Phase3Tests {

    /** One account with 120,000 holds. */
    @Test(order = 1, timeoutMs = 6000)
    void oneBusyAccount() {
        HoldBook book = Datasets.oneBusyAccount("MEGA", 120_000, 42L);
        ExposureCalculator calculator = new ExposureCalculator(book);
        assertEquals(EXPECTED_BUSY_PEAK, calculator.peak("MEGA"));
        assertEquals(OptionalInt.of(EXPECTED_BUSY_BREACH), calculator.firstBreach("MEGA", 7_000_000_000L));
        assertEquals(OptionalInt.empty(), calculator.firstBreach("MEGA", EXPECTED_BUSY_PEAK.amountMinor()));
    }

    /** 40,000 accounts with 5 holds each, then a batch of early releases. */
    @Test(order = 2, timeoutMs = 4000)
    void manySmallAccounts() {
        HoldBook book = Datasets.manySmallAccounts(40_000, 5, 7L);
        for (int a = 0; a < 40_000; a += 4) {
            book.release("S" + a + "-2");
        }
        Map<String, Peak> peaks = new ExposureCalculator(book).peakByAccount();
        assertEquals(40_000, peaks.size());
        long checksum = 0;
        for (Peak peak : peaks.values()) {
            checksum += peak.amountMinor() * 7 + peak.atMinute();
        }
        assertEquals(EXPECTED_SMALL_CHECKSUM, checksum, "checksum over 40,000 peaks");
    }

    static final Peak EXPECTED_BUSY_PEAK = new Peak(9_925_374_614L, 475_829);
    static final int EXPECTED_BUSY_BREACH = 3_048;
    static final long EXPECTED_SMALL_CHECKSUM = 328_497_025_692L;
}
