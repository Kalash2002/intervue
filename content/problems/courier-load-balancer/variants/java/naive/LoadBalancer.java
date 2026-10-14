import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Naive solution: correct, and slow on purpose. It scans every courier to find the lightest one for
 * every parcel, O(n * m). Phases 1 and 2 pass; phase 3 must time out.
 */
public class LoadBalancer {
    private final List<String> couriers;

    public LoadBalancer(List<String> couriers) {
        if (couriers.isEmpty()) {
            throw new IllegalArgumentException("a hub needs at least one courier");
        }
        this.couriers = List.copyOf(couriers);
    }

    public Plan balance(List<Parcel> parcels) {
        return plan(parcels, Long.MAX_VALUE);
    }

    public Plan balanceWithCapacity(List<Parcel> parcels, long capacityGrams) {
        if (capacityGrams <= 0) {
            throw new IllegalArgumentException("capacity must be positive: " + capacityGrams);
        }
        return plan(parcels, capacityGrams);
    }

    private Plan plan(List<Parcel> parcels, long capacityGrams) {
        Plan plan = new Plan(couriers);
        long[] load = new long[couriers.size()];
        for (Parcel parcel : heaviestFirst(parcels)) {
            int best = 0;
            for (int i = 1; i < load.length; i++) {
                if (load[i] < load[best]) {
                    best = i;
                }
            }
            if (load[best] + parcel.weightGrams() > capacityGrams) {
                plan.leaveOver(parcel);
                continue;
            }
            load[best] += parcel.weightGrams();
            plan.give(couriers.get(best), parcel);
        }
        return plan;
    }

    static List<Parcel> heaviestFirst(List<Parcel> parcels) {
        List<Parcel> sorted = new ArrayList<>(parcels);
        sorted.sort(Comparator.comparingInt(Parcel::weightGrams).reversed().thenComparing(Parcel::id));
        return sorted;
    }
}
