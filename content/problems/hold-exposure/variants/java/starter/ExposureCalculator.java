import java.util.LinkedHashMap;
import java.util.Map;
import java.util.OptionalInt;

/** Answers risk questions about the money currently held on accounts. */
public class ExposureCalculator {
    private final HoldBook book;

    public ExposureCalculator(HoldBook book) {
        this.book = book;
    }

    /** Total amount held on the account at the given minute. */
    public long exposureAt(String account, int minute) {
        int total = 0;
        for (Hold hold : book.holdsFor(account)) {
            if (hold.activeAt(minute)) {
                total += hold.amountMinor();
            }
        }
        return total;
    }

    /** The highest total held on the account at any minute. See the brief for the full rules. */
    public Peak peak(String account) {
        throw new UnsupportedOperationException("peak is not implemented yet");
    }

    /** The first minute at which the total held is greater than the limit. See the brief. */
    public OptionalInt firstBreach(String account, long limitMinor) {
        throw new UnsupportedOperationException("firstBreach is not implemented yet");
    }

    /** The peak of every account that has holds. */
    public Map<String, Peak> peakByAccount() {
        Map<String, Peak> peaks = new LinkedHashMap<>();
        for (String account : book.accounts()) {
            peaks.put(account, peak(account));
        }
        return peaks;
    }
}
