import java.util.List;
import java.util.Map;

/** Answers questions about how the access log measures up against the rate limit. */
public class RateAudit {
    private final RequestLog log;

    public RateAudit(RequestLog log) {
        this.log = log;
    }

    /** Requests of the client in the window of {@code windowMs} ending at {@code endMs}: start excluded, end included. */
    public int countInWindow(String client, long endMs, long windowMs) {
        int count = 0;
        for (long at : log.forClient(client)) {
            if (at > endMs - windowMs && at <= endMs) {
                count++;
            }
        }
        return count;
    }

    /** Whether the client made more than {@code limit} requests in that window. */
    public boolean isOverLimit(String client, long endMs, int limit, long windowMs) {
        return countInWindow(client, endMs, windowMs) > limit;
    }

    /** Times of the client's requests the limiter rejects. See the brief. */
    public List<Long> rejections(String client, int limit, long windowMs) {
        throw new UnsupportedOperationException("rejections is not implemented yet");
    }

    /** Rejections per client, for clients with any. See the brief. */
    public Map<String, Integer> rejectionCounts(int limit, long windowMs) {
        throw new UnsupportedOperationException("rejectionCounts is not implemented yet");
    }
}
