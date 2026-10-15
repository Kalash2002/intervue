import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Reports on orders that repeat an earlier basket. One pass over the log per report. */
public class DuplicateDetector {
    private final OrderLog log;

    public DuplicateDetector(OrderLog log) {
        this.log = log;
    }

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

    public int distinctBaskets() {
        Set<OrderKey> seen = new HashSet<>();
        for (Order order : log.all()) {
            seen.add(order.key());
        }
        return seen.size();
    }

    /** The map keeps only the latest minute per basket, which is all the window rule needs. */
    public List<String> suspectedDuplicates(int windowMinutes) {
        if (windowMinutes <= 0) {
            throw new IllegalArgumentException("window must be positive: " + windowMinutes);
        }
        Map<OrderKey, Integer> lastMinute = new HashMap<>();
        List<String> suspected = new ArrayList<>();
        for (Order order : log.all()) {
            Integer previous = lastMinute.put(order.key(), order.minute());
            if (previous != null && order.minute() - previous < windowMinutes) {
                suspected.add(order.id());
            }
        }
        return suspected;
    }

    public List<SkuCount> mostDuplicatedSkus(int k) {
        if (k < 0) {
            throw new IllegalArgumentException("k cannot be negative: " + k);
        }
        Set<OrderKey> seen = new HashSet<>();
        Map<String, Integer> counts = new HashMap<>();
        for (Order order : log.all()) {
            if (!seen.add(order.key())) {
                counts.merge(order.sku(), 1, Integer::sum);
            }
        }
        return counts.entrySet().stream()
                .map(e -> new SkuCount(e.getKey(), e.getValue()))
                .sorted(Comparator.comparingInt(SkuCount::count).reversed().thenComparing(SkuCount::sku))
                .limit(k)
                .toList();
    }
}
