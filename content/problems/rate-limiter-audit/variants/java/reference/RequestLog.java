import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Every request, oldest first, indexed by client so one client's times cost nothing to find. */
public class RequestLog {
    private final Map<String, List<Long>> byClient = new LinkedHashMap<>();
    private long lastAt = -1;
    private int size;

    public void add(Request request) {
        if (request.atMs() < lastAt) {
            throw new IllegalArgumentException("request at " + request.atMs() + " arrived out of order");
        }
        lastAt = request.atMs();
        byClient.computeIfAbsent(request.client(), c -> new ArrayList<>()).add(request.atMs());
        size++;
    }

    public List<Long> forClient(String client) {
        return Collections.unmodifiableList(byClient.getOrDefault(client, List.of()));
    }

    public Set<String> clients() {
        return Collections.unmodifiableSet(byClient.keySet());
    }

    public int size() {
        return size;
    }
}
