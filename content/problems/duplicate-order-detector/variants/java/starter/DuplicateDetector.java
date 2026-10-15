import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Reports on orders that repeat an earlier basket. */
public class DuplicateDetector {
    private final OrderLog log;

    public DuplicateDetector(OrderLog log) {
        this.log = log;
    }

    /** Ids of orders whose basket appeared earlier in the log, at any time; in log order. */
    public List<String> exactRepeats() {
        Set<OrderKey> seen = new HashSet<>();
        List<String> repeats = new ArrayList<>();
        for (Order order : log.all()) {
            if (!seen.add(order.key())) {
                repeats.add(order.id());
            }
        }
        return repeats;
    }

    /** How many different baskets the log holds. */
    public int distinctBaskets() {
        Set<OrderKey> seen = new HashSet<>();
        for (Order order : log.all()) {
            seen.add(order.key());
        }
        return seen.size();
    }

    /** Orders repeating the most recent copy of their basket within the window. See the brief. */
    public List<String> suspectedDuplicates(int windowMinutes) {
        throw new UnsupportedOperationException("suspectedDuplicates is not implemented yet");
    }

    /** The SKUs with the most repeat orders. See the brief. */
    public List<SkuCount> mostDuplicatedSkus(int k) {
        throw new UnsupportedOperationException("mostDuplicatedSkus is not implemented yet");
    }
}
