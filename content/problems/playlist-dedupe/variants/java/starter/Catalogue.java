import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Every track by id, in the order it was imported. */
public class Catalogue {
    private final Map<String, Track> byId = new HashMap<>();
    private final List<String> order = new ArrayList<>();

    public void add(Track track) {
        if (byId.putIfAbsent(track.id(), track) != null) {
            throw new IllegalArgumentException("duplicate track id " + track.id());
        }
        order.add(track.id());
    }

    public Optional<Track> find(String id) {
        return Optional.ofNullable(byId.get(id));
    }

    /** Every track id, in import order. */
    public List<String> ids() {
        return Collections.unmodifiableList(order);
    }

    public int size() {
        return order.size();
    }
}
