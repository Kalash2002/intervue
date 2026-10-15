import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

/**
 * Naive solution: correct, and slow on purpose. Every lookup walks the ward's shifts from the first,
 * O(n) per query. Phases 1 and 2 pass; phase 3 must time out.
 */
public class RosterLookup {
    private final Roster roster;

    public RosterLookup(Roster roster) {
        this.roster = roster;
    }

    public Optional<String> onDutyAt(String ward, int minute) {
        for (Shift shift : roster.shifts(ward)) {
            if (shift.covers(minute)) {
                return Optional.of(shift.nurse());
            }
        }
        return Optional.empty();
    }

    public OptionalInt nextHandover(String ward, int minute) {
        for (Shift shift : roster.shifts(ward)) {
            if (shift.startMinute() > minute) {
                return OptionalInt.of(shift.startMinute());
            }
        }
        return OptionalInt.empty();
    }

    public List<Gap> uncovered(String ward, int fromMinute, int toMinute) {
        if (fromMinute >= toMinute) {
            throw new IllegalArgumentException("empty range: " + fromMinute + " to " + toMinute);
        }
        List<Gap> gaps = new ArrayList<>();
        int covered = fromMinute;
        for (Shift shift : roster.shifts(ward)) {
            if (shift.endMinute() <= covered) {
                continue;
            }
            if (shift.startMinute() >= toMinute) {
                break;
            }
            if (shift.startMinute() > covered) {
                gaps.add(new Gap(covered, shift.startMinute()));
            }
            covered = shift.endMinute();
        }
        if (covered < toMinute) {
            gaps.add(new Gap(covered, toMinute));
        }
        return gaps;
    }
}
