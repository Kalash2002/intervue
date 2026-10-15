import java.util.ArrayList;
import java.util.List;

/**
 * Naive solution: correct, and slow on purpose. Every report looks back over all earlier orders for
 * each order, so each is O(n^2). Phases 1 and 2 pass; phase 3 must time out.
 */
public class DuplicateDetector {
    private final OrderLog log;

    public DuplicateDetector(OrderLog log) {
        this.log = log;
    }

    public List<String> exactRepeats() {
        List<Order> orders = log.all();
        List<String> repeats = new ArrayList<>();
        for (int i = 0; i < orders.size(); i++) {
            if (previousCopy(orders, i) >= 0) {
                repeats.add(orders.get(i).id());
            }
        }
        return repeats;
    }

    public int distinctBaskets() {
        return log.size() - exactRepeats().size();
    }

    public List<String> suspectedDuplicates(int windowMinutes) {
        if (windowMinutes <= 0) {
            throw new IllegalArgumentException("window must be positive: " + windowMinutes);
        }
        List<Order> orders = log.all();
        List<String> suspected = new ArrayList<>();
        for (int i = 0; i < orders.size(); i++) {
            int previous = previousCopy(orders, i);
            if (previous >= 0 && orders.get(i).minute() - orders.get(previous).minute() < windowMinutes) {
                suspected.add(orders.get(i).id());
            }
        }
        return suspected;
    }

    public List<SkuCount> mostDuplicatedSkus(int k) {
        if (k < 0) {
            throw new IllegalArgumentException("k cannot be negative: " + k);
        }
        List<Order> orders = log.all();
        List<SkuCount> counts = new ArrayList<>();
        for (int i = 0; i < orders.size(); i++) {
            if (previousCopy(orders, i) < 0) {
                continue;
            }
            String sku = orders.get(i).sku();
            int at = -1;
            for (int c = 0; c < counts.size(); c++) {
                if (counts.get(c).sku().equals(sku)) {
                    at = c;
                }
            }
            if (at < 0) {
                counts.add(new SkuCount(sku, 1));
            } else {
                counts.set(at, new SkuCount(sku, counts.get(at).count() + 1));
            }
        }
        counts.sort((a, b) -> a.count() != b.count() ? b.count() - a.count() : a.sku().compareTo(b.sku()));
        return counts.subList(0, Math.min(k, counts.size()));
    }

    /** Index of the most recent earlier order with the same basket, or -1. */
    private static int previousCopy(List<Order> orders, int i) {
        OrderKey key = orders.get(i).key();
        for (int j = i - 1; j >= 0; j--) {
            if (orders.get(j).key().equals(key)) {
                return j;
            }
        }
        return -1;
    }
}
