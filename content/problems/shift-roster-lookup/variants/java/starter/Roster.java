import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Every ward's shifts sorted by start minute, checked for overlaps when the roster is built. */
public final class Roster {
    private final Map<String, List<Shift>> shifts = new HashMap<>();
    private final Map<String, List<Integer>> starts = new HashMap<>();

    private Roster() {}

    public static Roster of(Collection<Shift> all) {
        Roster roster = new Roster();
        for (Shift shift : all) {
            roster.shifts.computeIfAbsent(shift.ward(), w -> new ArrayList<>()).add(shift);
        }
        for (Map.Entry<String, List<Shift>> e : roster.shifts.entrySet()) {
            List<Shift> ward = e.getValue();
            ward.sort(Comparator.comparingInt(Shift::startMinute));
            List<Integer> wardStarts = new ArrayList<>(ward.size());
            for (int i = 0; i < ward.size(); i++) {
                if (i > 0 && ward.get(i).startMinute() < ward.get(i - 1).endMinute()) {
                    throw new IllegalArgumentException("overlapping shifts in " + e.getKey() + " at minute "
                            + ward.get(i).startMinute());
                }
                wardStarts.add(ward.get(i).startMinute());
            }
            roster.starts.put(e.getKey(), wardStarts);
        }
        return roster;
    }

    /** The ward's shifts, earliest first; empty for an unknown ward. */
    public List<Shift> shifts(String ward) {
        return shifts.getOrDefault(ward, List.of());
    }

    /** The start minute of each of those shifts, in the same order. */
    public List<Integer> starts(String ward) {
        return starts.getOrDefault(ward, List.of());
    }

    public Set<String> wards() {
        return shifts.keySet();
    }
}
