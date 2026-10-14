import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;

/** Reference solution: one sorted sweep over start and end events. */
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
        Event[] events = events(account);
        long running = 0;
        long best = 0;
        int bestMinute = -1;
        for (Event event : events) {
            running += event.delta;
            if (running > best) {
                best = running;
                bestMinute = event.minute;
            }
        }
        return bestMinute < 0 ? Peak.NONE : new Peak(best, bestMinute);
    }

    public OptionalInt firstBreach(String account, long limitMinor) {
        if (limitMinor < 0) {
            throw new IllegalArgumentException("limit cannot be negative: " + limitMinor);
        }
        long running = 0;
        for (Event event : events(account)) {
            running += event.delta;
            if (running > limitMinor) {
                return OptionalInt.of(event.minute);
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

    private record Event(int minute, long delta) {}

    /**
     * Two events per hold, sorted by minute. Within one minute, ends come before starts, because a
     * hold is no longer active at its end minute. That ordering is what makes touching holds not overlap.
     */
    private Event[] events(String account) {
        List<Hold> holds = book.holdsFor(account);
        Event[] events = new Event[holds.size() * 2];
        int i = 0;
        for (Hold hold : holds) {
            events[i++] = new Event(hold.startMinute(), hold.amountMinor());
            events[i++] = new Event(hold.endMinute(), -hold.amountMinor());
        }
        Arrays.sort(events, (a, b) -> a.minute != b.minute ? Integer.compare(a.minute, b.minute) : Long.compare(a.delta, b.delta));
        return events;
    }
}
