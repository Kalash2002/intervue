import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * All merchants, kept sorted by normalized name so that a merchant can be found by binary search.
 * Two merchants cannot share a normalized name: adding one replaces the other.
 */
public class MerchantDirectory {
    private final List<String> keys = new ArrayList<>();
    private final List<Merchant> merchants = new ArrayList<>();

    public void add(Merchant merchant) {
        String key = Normalizer.normalize(merchant.name());
        int position = Collections.binarySearch(keys, key);
        if (position >= 0) {
            merchants.set(position, merchant);
        } else {
            int insertAt = -position - 1;
            keys.add(insertAt, key);
            merchants.add(insertAt, merchant);
        }
    }

    /** The merchant whose normalized name equals the normalized form of {@code name}. */
    public Optional<Merchant> find(String name) {
        String key = Normalizer.normalize(name);
        int low = 0;
        int high = keys.size() - 1;
        while (low < high) {
            int middle = (low + high) >>> 1;
            int order = keys.get(middle).compareTo(key);
            if (order == 0) {
                return Optional.of(merchants.get(middle));
            }
            if (order < 0) {
                low = middle + 1;
            } else {
                high = middle - 1;
            }
        }
        return Optional.empty();
    }

    /** Every merchant, ordered by normalized name. */
    public List<Merchant> all() {
        return Collections.unmodifiableList(merchants);
    }

    public int size() {
        return merchants.size();
    }
}
