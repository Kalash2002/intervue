import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.TreeMap;

/** Reference solution: a sorted map, so adding is O(log n) instead of shifting an array list. */
public class MerchantDirectory {
    private final TreeMap<String, Merchant> byKey = new TreeMap<>();
    private List<Merchant> sortedView;
    private long version;

    public void add(Merchant merchant) {
        byKey.put(Normalizer.normalize(merchant.name()), merchant);
        sortedView = null;
        version++;
    }

    public Optional<Merchant> find(String name) {
        return Optional.ofNullable(byKey.get(Normalizer.normalize(name)));
    }

    public List<Merchant> all() {
        if (sortedView == null) {
            sortedView = Collections.unmodifiableList(new ArrayList<>(byKey.values()));
        }
        return sortedView;
    }

    public int size() {
        return byKey.size();
    }

    /** Changes every time the directory changes; lets other classes know their derived data is stale. */
    public long version() {
        return version;
    }
}
