/** One access-log line: which client called, and when, in milliseconds since the log started. */
public record Request(String client, long atMs) {
    public Request {
        if (atMs < 0) {
            throw new IllegalArgumentException("request time cannot be negative: " + atMs);
        }
    }
}
