import java.util.Random;

/** Builds the large access logs used by the phase 3 tests. Same seed, same log. */
public final class Datasets {
    private Datasets() {}

    /** One client calling in bursts: gaps of 0 to 3 ms between requests. */
    public static RequestLog noisyClient(String client, int requests, long seed) {
        Random random = new Random(seed);
        RequestLog log = new RequestLog();
        long at = 0;
        for (int i = 0; i < requests; i++) {
            at += random.nextInt(4);
            log.add(new Request(client, at));
        }
        return log;
    }

    /** Many clients, each calling a few times, interleaved as a gateway sees them. */
    public static RequestLog manyClients(int clients, int requestsEach, long seed) {
        Random random = new Random(seed);
        RequestLog log = new RequestLog();
        long at = 0;
        for (int round = 0; round < requestsEach; round++) {
            for (int c = 0; c < clients; c++) {
                at += random.nextInt(2);
                log.add(new Request("K" + random.nextInt(clients), at));
            }
        }
        return log;
    }
}
