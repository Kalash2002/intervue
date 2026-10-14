import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

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
        return plan(parcels, Long.MAX_VALUE);
    }

    public Plan balanceWithCapacity(List<Parcel> parcels, long capacityGrams) {
        if (capacityGrams <= 0) {
            throw new IllegalArgumentException("capacity must be positive: " + capacityGrams);
        }
        return plan(parcels, capacityGrams);
    }

    /**
     * A min-heap of {load, courier index}: the lightest courier is on top, and equal loads come out
     * by index, which is the "listed first" tie-break. O(n log m) instead of a scan per parcel.
     */
    private Plan plan(List<Parcel> parcels, long capacityGrams) {
        Plan plan = new Plan(couriers);
        PriorityQueue<long[]> lightest = new PriorityQueue<>(
                Comparator.<long[]>comparingLong(c -> c[0]).thenComparingLong(c -> c[1]));
        for (int i = 0; i < couriers.size(); i++) {
            lightest.add(new long[] {0, i});
        }
        for (Parcel parcel : heaviestFirst(parcels)) {
            long[] courier = lightest.peek();
            if (courier[0] + parcel.weightGrams() > capacityGrams) {
                plan.leaveOver(parcel);
                continue;
            }
            lightest.poll();
            courier[0] += parcel.weightGrams();
            plan.give(couriers.get((int) courier[1]), parcel);
            lightest.add(courier);
        }
        return plan;
    }

    static List<Parcel> heaviestFirst(List<Parcel> parcels) {
        List<Parcel> sorted = new ArrayList<>(parcels);
        sorted.sort(Comparator.comparingInt(Parcel::weightGrams).reversed().thenComparing(Parcel::id));
        return sorted;
    }
}
