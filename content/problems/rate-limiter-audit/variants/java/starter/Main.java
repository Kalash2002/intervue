import java.nio.file.Path;

/** Small demo. Run it from the Run menu to see window counts on the sample log. */
public class Main {
    public static void main(String[] args) throws Exception {
        RequestLog log = LogParser.fromFile(Path.of("data/requests.csv"));
        RateAudit audit = new RateAudit(log);
        System.out.println(log.size() + " requests from " + log.clients().size() + " clients");
        for (String client : log.clients()) {
            System.out.println(client + " times: " + log.forClient(client));
            for (long end : new long[] {1000, 2005, 3000}) {
                System.out.println("  1 s window ending at " + end + " ms: " + audit.countInWindow(client, end, 1000));
            }
        }
    }
}
