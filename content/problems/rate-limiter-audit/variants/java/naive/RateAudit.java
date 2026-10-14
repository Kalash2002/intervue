import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Naive solution: correct, and slow on purpose. For every request it counts all accepted requests
 * from the start, so one client is O(n^2); RequestLog is left as in the starter, so many clients
 * also pay a full log scan each. Phases 1 and 2 pass; phase 3 must time out.
 */
public class RateAudit {
    private final RequestLog log;

    public RateAudit(RequestLog log) {
        this.log = log;
    }

    public int countInWindow(String client, long endMs, long windowMs) {
        int count = 0;
        for (long at : log.forClient(client)) {
            if (at > endMs - windowMs && at <= endMs) {
                count++;
            }
        }
        return count;
    }

    public boolean isOverLimit(String client, long endMs, int limit, long windowMs) {
        return countInWindow(client, endMs, windowMs) > limit;
    }

    public List<Long> rejections(String client, int limit, long windowMs) {
        check(limit, windowMs);
        List<Long> accepted = new ArrayList<>();
        List<Long> rejected = new ArrayList<>();
        for (long at : log.forClient(client)) {
            int inWindow = 0;
            for (long earlier : accepted) {
                if (earlier > at - windowMs) {
                    inWindow++;
                }
            }
            if (inWindow >= limit) {
                rejected.add(at);
            } else {
                accepted.add(at);
            }
        }
        return rejected;
    }

    public Map<String, Integer> rejectionCounts(int limit, long windowMs) {
        check(limit, windowMs);
        Map<String, Integer> counts = new TreeMap<>();
        for (String client : log.clients()) {
            int n = rejections(client, limit, windowMs).size();
            if (n > 0) {
                counts.put(client, n);
            }
        }
        return counts;
    }

    private static void check(int limit, long windowMs) {
        if (limit < 1 || windowMs < 1) {
            throw new IllegalArgumentException("limit and window must be at least 1: " + limit + ", " + windowMs);
        }
    }
}
