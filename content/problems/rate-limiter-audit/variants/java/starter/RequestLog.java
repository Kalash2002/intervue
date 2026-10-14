import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/** Every request, oldest first. Times never go backwards. */
public class RequestLog {
    private final List<Request> requests = new ArrayList<>();

    public void add(Request request) {
        if (!requests.isEmpty() && request.atMs() < requests.get(requests.size() - 1).atMs()) {
            throw new IllegalArgumentException("request at " + request.atMs() + " arrived out of order");
        }
        requests.add(request);
    }

    /** The client's request times, oldest first. */
    public List<Long> forClient(String client) {
        List<Long> times = new ArrayList<>();
        for (Request request : requests) {
            if (request.client().equals(client)) {
                times.add(request.atMs());
            }
        }
        return times;
    }

    /** Every client in the order it first appears. */
    public Set<String> clients() {
        Set<String> clients = new LinkedHashSet<>();
        for (Request request : requests) {
            clients.add(request.client());
        }
        return clients;
    }

    public int size() {
        return requests.size();
    }
}
