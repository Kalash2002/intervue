import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/** Builds the large rosters and query streams used by the phase 3 tests. Same seed, same data. */
public final class Datasets {
    private Datasets() {}

    /** Wards W0..W(n-1), each with back-to-back shifts of 4 to 12 hours and an occasional gap. */
    public static Roster roster(int wards, int shiftsPerWard, long seed) {
        Random random = new Random(seed);
        List<Shift> shifts = new ArrayList<>(wards * shiftsPerWard);
        for (int w = 0; w < wards; w++) {
            int minute = 0;
            for (int s = 0; s < shiftsPerWard; s++) {
                if (random.nextInt(10) == 0) {
                    minute += 30 + random.nextInt(120);
                }
                int length = 240 + random.nextInt(481);
                shifts.add(new Shift("N" + random.nextInt(5_000), "W" + w, minute, minute + length));
                minute += length;
            }
        }
        return Roster.of(shifts);
    }

    /** Query minutes spread over the whole roster, one ward per query in turn. */
    public static int[] minutes(int queries, int maxMinute, long seed) {
        Random random = new Random(seed);
        int[] minutes = new int[queries];
        for (int i = 0; i < queries; i++) {
            minutes[i] = random.nextInt(maxMinute);
        }
        return minutes;
    }
}
