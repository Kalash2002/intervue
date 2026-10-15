import static kit.Check.assertEquals;

import java.util.OptionalInt;
import kit.Test;

/** Five wards with 50,000 shifts each. Each test has a time limit; a slow solution fails with TIMEOUT. */
public class Phase3Tests {

    @Test(order = 1, timeoutMs = 3000)
    void halfAMillionDutyLookups() {
        Roster roster = Datasets.roster(5, 50_000, 71L);
        RosterLookup lookup = new RosterLookup(roster);
        int[] minutes = Datasets.minutes(500_000, 25_000_000, 72L);
        long checksum = 0;
        int nobody = 0;
        for (int i = 0; i < minutes.length; i++) {
            String nurse = lookup.onDutyAt("W" + (i % 5), minutes[i]).orElse(null);
            if (nurse == null) {
                nobody++;
            } else {
                checksum = checksum * 31 + nurse.hashCode();
            }
        }
        assertEquals(EXPECTED_NOBODY, nobody, "lookups with nobody on duty");
        assertEquals(EXPECTED_DUTY_CHECKSUM, checksum, "checksum over nurses");
    }

    @Test(order = 2, timeoutMs = 3000)
    void halfAMillionHandoverLookups() {
        Roster roster = Datasets.roster(5, 50_000, 73L);
        RosterLookup lookup = new RosterLookup(roster);
        int[] minutes = Datasets.minutes(500_000, 25_000_000, 74L);
        long sum = 0;
        int none = 0;
        for (int i = 0; i < minutes.length; i++) {
            OptionalInt next = lookup.nextHandover("W" + (i % 5), minutes[i]);
            if (next.isPresent()) {
                sum += next.getAsInt() - minutes[i];
            } else {
                none++;
            }
        }
        assertEquals(EXPECTED_NONE, none, "lookups with no later handover");
        assertEquals(EXPECTED_WAIT_SUM, sum, "total minutes until the next handover");
    }

    static final int EXPECTED_NOBODY = 20_183;
    static final long EXPECTED_DUTY_CHECKSUM = -6_171_971_936_017_383_535L;
    static final int EXPECTED_NONE = 11_221;
    static final long EXPECTED_WAIT_SUM = 129_597_072L;
}
