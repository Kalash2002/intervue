import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Splits parcels between couriers: heaviest parcel first, to the lightest-loaded courier. */
public class LoadBalancer {
    private final List<String> couriers;

    public LoadBalancer(List<String> couriers) {
        if (couriers.isEmpty()) {
            throw new IllegalArgumentException("a hub needs at least one courier");
        }
        this.couriers = List.copyOf(couriers);
    }

    public Plan balance(List<Parcel> parcels) {
        Plan plan = new Plan(couriers);
        long[] load = new long[couriers.size()];
        for (Parcel parcel : heaviestFirst(parcels)) {
            int best = 0;
            for (int i = 1; i < load.length; i++) {
                if (load[i] <= load[best]) {
                    best = i;
                }
            }
            load[best] += parcel.weightGrams();
            plan.give(couriers.get(best), parcel);
        }
        return plan;
    }

    /** Like balance, but no van carries more than the capacity. See the brief. */
    public Plan balanceWithCapacity(List<Parcel> parcels, long capacityGrams) {
        throw new UnsupportedOperationException("balanceWithCapacity is not implemented yet");
    }

    /** Heaviest first; equal weights in order of id. */
    static List<Parcel> heaviestFirst(List<Parcel> parcels) {
        List<Parcel> sorted = new ArrayList<>(parcels);
        sorted.sort(Comparator.comparingInt(Parcel::weightGrams).reversed().thenComparing(Parcel::id));
        return sorted;
    }
}
