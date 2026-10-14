import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

/** The questions the app asks about the roster. Every lookup is a binary search over start minutes. */
public class RosterLookup {
    private final Roster roster;

    public RosterLookup(Roster roster) {
        this.roster = roster;
    }

    public Optional<String> onDutyAt(String ward, int minute) {
        int index = lastStartAtOrBefore(roster.starts(ward), minute);
        if (index < 0) {
            return Optional.empty();
        }
        Shift shift = roster.shifts(ward).get(index);
        return shift.covers(minute) ? Optional.of(shift.nurse()) : Optional.empty();
    }

    public OptionalInt nextHandover(String ward, int minute) {
        List<Integer> starts = roster.starts(ward);
        int next = lastStartAtOrBefore(starts, minute) + 1;
        return next < starts.size() ? OptionalInt.of(starts.get(next)) : OptionalInt.empty();
    }

    /** Starts at the shift covering (or last before) fromMinute and walks forward only through the range. */
    public List<Gap> uncovered(String ward, int fromMinute, int toMinute) {
        if (fromMinute >= toMinute) {
            throw new IllegalArgumentException("empty range: " + fromMinute + " to " + toMinute);
        }
        List<Shift> shifts = roster.shifts(ward);
        List<Gap> gaps = new ArrayList<>();
        int covered = fromMinute;
        for (int i = Math.max(0, lastStartAtOrBefore(roster.starts(ward), fromMinute)); i < shifts.size(); i++) {
            Shift shift = shifts.get(i);
            if (shift.startMinute() >= toMinute) {
                break;
            }
            if (shift.startMinute() > covered) {
                gaps.add(new Gap(covered, shift.startMinute()));
            }
            covered = Math.max(covered, shift.endMinute());
        }
        if (covered < toMinute) {
            gaps.add(new Gap(covered, toMinute));
        }
        return gaps;
    }

    /**
     * Index of the last shift starting at or before the minute, or -1. When the minute is not a start,
     * binarySearch returns -(insertion point) - 1, and the shift before the insertion point is the one.
     */
    private static int lastStartAtOrBefore(List<Integer> starts, int minute) {
        int found = Collections.binarySearch(starts, minute);
        return found >= 0 ? found : -found - 2;
    }
}
