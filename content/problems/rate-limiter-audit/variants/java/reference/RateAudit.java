import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/** Answers questions about how the access log measures up against the rate limit. */
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

    /** A sliding window of accepted times: each time enters and leaves the deque once, so O(n). */
    public List<Long> rejections(String client, int limit, long windowMs) {
        check(limit, windowMs);
        Deque<Long> accepted = new ArrayDeque<>();
        List<Long> rejected = new ArrayList<>();
        for (long at : log.forClient(client)) {
            while (!accepted.isEmpty() && accepted.peekFirst() <= at - windowMs) {
                accepted.pollFirst();
            }
            if (accepted.size() >= limit) {
                rejected.add(at);
            } else {
                accepted.addLast(at);
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
