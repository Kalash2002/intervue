import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/** Builds the large hubs used by the phase 3 tests. Same seed, same hub. */
public final class Datasets {
    private Datasets() {}

    /** Courier ids C0..C(n-1), in list order. */
    public static List<String> couriers(int n) {
        List<String> couriers = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            couriers.add("C" + i);
        }
        return couriers;
    }

    /** Parcels between 100 g and 30 kg, most of them light. */
    public static List<Parcel> parcels(int n, long seed) {
        Random random = new Random(seed);
        List<Parcel> parcels = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            int grams = random.nextInt(10) == 0 ? 5_000 + random.nextInt(25_000) : 100 + random.nextInt(4_900);
            parcels.add(new Parcel("P" + i, grams));
        }
        return parcels;
    }
}
