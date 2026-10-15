import static kit.Check.assertEquals;
import static kit.Check.assertThrows;

import java.nio.file.Path;
import java.util.List;
import java.util.OptionalInt;
import kit.Test;

public class Phase2Tests {

    private static RosterLookup sample() throws Exception {
        return new RosterLookup(RosterParser.fromFile(Path.of("data/roster.csv")));
    }

    @Test(order = 1)
    void nextHandoverIsStrictlyLater() throws Exception {
        RosterLookup lookup = sample();
        assertEquals(OptionalInt.of(480), lookup.nextHandover("ICU", 100));
        assertEquals(OptionalInt.of(1020), lookup.nextHandover("ICU", 480));
        assertEquals(OptionalInt.of(1020), lookup.nextHandover("ICU", 990));
        assertEquals(OptionalInt.of(0), lookup.nextHandover("WARD-2", -5));
    }

    @Test(order = 2)
    void noHandoverAfterTheLastShift() throws Exception {
        RosterLookup lookup = sample();
        assertEquals(OptionalInt.empty(), lookup.nextHandover("ICU", 1020));
        assertEquals(OptionalInt.empty(), lookup.nextHandover("PHARMACY", 0));
    }

    @Test(order = 3)
    void gapsBetweenAndAfterShifts() throws Exception {
        assertEquals(List.of(new Gap(960, 1020), new Gap(1500, 1600)), sample().uncovered("ICU", 0, 1600));
    }

    @Test(order = 4)
    void gapsAreCutToTheRange() throws Exception {
        RosterLookup lookup = sample();
        assertEquals(List.of(new Gap(970, 1010)), lookup.uncovered("ICU", 970, 1010));
        assertEquals(List.of(new Gap(990, 1020)), lookup.uncovered("ICU", 990, 1100));
        assertEquals(List.of(new Gap(960, 1020)), lookup.uncovered("ICU", 500, 1100));
        assertEquals(List.of(), lookup.uncovered("WARD-2", 0, 1440));
    }

    @Test(order = 5)
    void beforeTheFirstShiftAndUnknownWards() {
        RosterLookup lookup = new RosterLookup(Roster.of(List.of(
                new Shift("ZOE", "ER", 100, 200), new Shift("YAN", "ER", 200, 300))));
        assertEquals(List.of(new Gap(0, 100), new Gap(300, 400)), lookup.uncovered("ER", 0, 400));
        assertEquals(List.of(new Gap(5, 10)), lookup.uncovered("PHARMACY", 5, 10));
    }

    @Test(order = 6)
    void anEmptyRangeIsRefused() throws Exception {
        RosterLookup lookup = sample();
        assertThrows(IllegalArgumentException.class, () -> lookup.uncovered("ICU", 10, 10));
        assertThrows(IllegalArgumentException.class, () -> lookup.uncovered("ICU", 10, 5));
    }
}
