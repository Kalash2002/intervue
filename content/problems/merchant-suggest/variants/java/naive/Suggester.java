import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Naive solution: correct, and slow on purpose. Every call normalizes and tests every merchant, then
 * sorts the matches. Nothing is indexed or remembered, so phase 3 times out on every workload.
 */
public class Suggester {
    private static final int MAX_LIMIT = 10;

    private final MerchantDirectory directory;

    public Suggester(MerchantDirectory directory) {
        this.directory = directory;
    }

    public List<Merchant> suggest(String typed, int limit) {
        if (limit < 1 || limit > MAX_LIMIT) {
            throw new IllegalArgumentException("limit must be between 1 and " + MAX_LIMIT + ": " + limit);
        }
        String prefix = Normalizer.normalize(typed);
        if (prefix.isEmpty()) {
            return List.of();
        }
        List<Merchant> matches = new ArrayList<>();
        for (Merchant merchant : directory.all()) { // sorted by normalized name
            String key = Normalizer.normalize(merchant.name());
            if (key.startsWith(prefix) || key.contains(" " + prefix)) {
                matches.add(merchant);
            }
        }
        // List.sort is stable, so merchants with equal counts keep their normalized-name order.
        matches.sort(Comparator.comparingLong(Merchant::txnCount).reversed());
        return List.copyOf(matches.subList(0, Math.min(limit, matches.size())));
    }
}
