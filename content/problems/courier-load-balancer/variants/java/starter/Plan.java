import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Which courier carries which parcels, in the order they were given, and parcels left over. Couriers
 * appear in the order they were listed, including those that carry nothing.
 */
public final class Plan {
    private final Map<String, List<String>> parcels = new LinkedHashMap<>();
    private final Map<String, Long> loads = new LinkedHashMap<>();
    private final List<String> leftOver = new ArrayList<>();

    public Plan(List<String> couriers) {
        for (String courier : couriers) {
            parcels.put(courier, new ArrayList<>());
            loads.put(courier, 0L);
        }
    }

    public void give(String courier, Parcel parcel) {
        parcels.get(courier).add(parcel.id());
        loads.merge(courier, (long) parcel.weightGrams(), Long::sum);
    }

    public void leaveOver(Parcel parcel) {
        leftOver.add(parcel.id());
    }

    public Map<String, List<String>> parcels() {
        return parcels;
    }

    public Map<String, Long> loads() {
        return loads;
    }

    public List<String> leftOver() {
        return leftOver;
    }

    /** The heaviest load of any courier. */
    public long heaviestLoad() {
        long max = 0;
        for (long load : loads.values()) {
            max = Math.max(max, load);
        }
        return max;
    }
}
