import java.util.LinkedHashMap;
import java.util.Map;
import java.util.OptionalInt;
import java.util.TreeSet;

/**
 * Naive solution: correct, and slow on purpose. The total can only rise at a start minute, so the peak
 * is the total at some start; this evaluates every start with a full scan, which is O(n^2) per account.
 * HoldBook is left as in the starter, so phase 3 also pays for its linear scans. Phases 1 and 2 pass;
 * phase 3 must time out.
 */
public class ExposureCalculator {
    private final HoldBook book;

    public ExposureCalculator(HoldBook book) {
        this.book = book;
    }

    public long exposureAt(String account, int minute) {
        long total = 0;
        for (Hold hold : book.holdsFor(account)) {
            if (hold.activeAt(minute)) {
                total += hold.amountMinor();
            }
        }
        return total;
    }

    public Peak peak(String account) {
        long best = 0;
        int bestMinute = -1;
        for (int minute : startMinutes(account)) {
            long total = exposureAt(account, minute);
            if (total > best) {
                best = total;
                bestMinute = minute;
            }
        }
        return bestMinute < 0 ? Peak.NONE : new Peak(best, bestMinute);
    }

    public OptionalInt firstBreach(String account, long limitMinor) {
        if (limitMinor < 0) {
            throw new IllegalArgumentException("limit cannot be negative: " + limitMinor);
        }
        for (int minute : startMinutes(account)) {
            if (exposureAt(account, minute) > limitMinor) {
                return OptionalInt.of(minute);
            }
        }
        return OptionalInt.empty();
    }

    public Map<String, Peak> peakByAccount() {
        Map<String, Peak> peaks = new LinkedHashMap<>();
        for (String account : book.accounts()) {
            peaks.put(account, peak(account));
        }
        return peaks;
    }

    /** Every distinct start minute of the account's holds, ascending, so the earliest peak wins. */
    private TreeSet<Integer> startMinutes(String account) {
        TreeSet<Integer> starts = new TreeSet<>();
        for (Hold hold : book.holdsFor(account)) {
            starts.add(hold.startMinute());
        }
        return starts;
    }
}
