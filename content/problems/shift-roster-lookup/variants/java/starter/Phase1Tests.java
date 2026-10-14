import static kit.Check.assertEquals;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import kit.Test;

public class Phase1Tests {

    private static RosterLookup sample() throws Exception {
        return new RosterLookup(RosterParser.fromFile(Path.of("data/roster.csv")));
    }

    @Test(order = 1)
    void theExactStartOfAShift() throws Exception {
        RosterLookup lookup = sample();
        assertEquals(Optional.of("ASHA"), lookup.onDutyAt("ICU", 0));
        assertEquals(Optional.of("BEN"), lookup.onDutyAt("ICU", 480));
        assertEquals(Optional.of("EVA"), lookup.onDutyAt("WARD-2", 720));
    }

    @Test(order = 2)
    void afterTheLastShiftAndInUnknownWards() throws Exception {
        RosterLookup lookup = sample();
        assertEquals(Optional.empty(), lookup.onDutyAt("ICU", 1500));
        assertEquals(Optional.empty(), lookup.onDutyAt("ICU", 9_999));
        assertEquals(Optional.empty(), lookup.onDutyAt("PHARMACY", 10));
    }

    @Test(order = 3)
    void theMiddleOfAShift() throws Exception {
        RosterLookup lookup = sample();
        assertEquals(Optional.of("ASHA"), lookup.onDutyAt("ICU", 100));
        assertEquals(Optional.of("BEN"), lookup.onDutyAt("ICU", 959));
        assertEquals(Optional.of("DEV"), lookup.onDutyAt("WARD-2", 719));
    }

    @Test(order = 4)
    void nobodyDuringAGap() throws Exception {
        assertEquals(Optional.empty(), sample().onDutyAt("ICU", 1000));
    }

    @Test(order = 5)
    void beforeTheFirstShift() {
        RosterLookup lookup = new RosterLookup(Roster.of(List.of(new Shift("ZOE", "ER", 100, 200))));
        assertEquals(Optional.empty(), lookup.onDutyAt("ER", 50));
        assertEquals(Optional.of("ZOE"), lookup.onDutyAt("ER", 150));
    }
}
