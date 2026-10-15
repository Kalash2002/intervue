import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

/** The questions the app asks about the roster. */
public class RosterLookup {
    private final Roster roster;

    public RosterLookup(Roster roster) {
        this.roster = roster;
    }

    /** The nurse on duty in the ward at the minute, or empty when nobody is. */
    public Optional<String> onDutyAt(String ward, int minute) {
        List<Integer> starts = roster.starts(ward);
        int found = Collections.binarySearch(starts, minute);
        // Exact start: that shift. Otherwise binarySearch encodes where the minute would go.
        int index = found >= 0 ? found : -found - 1;
        if (index < 0 || index >= starts.size()) {
            return Optional.empty();
        }
        Shift shift = roster.shifts(ward).get(index);
        return minute < shift.endMinute() ? Optional.of(shift.nurse()) : Optional.empty();
    }

    /** The first shift start strictly after the minute. See the brief. */
    public OptionalInt nextHandover(String ward, int minute) {
        throw new UnsupportedOperationException("nextHandover is not implemented yet");
    }

    /** Stretches of the range nobody covers. See the brief. */
    public List<Gap> uncovered(String ward, int fromMinute, int toMinute) {
        throw new UnsupportedOperationException("uncovered is not implemented yet");
    }
}
