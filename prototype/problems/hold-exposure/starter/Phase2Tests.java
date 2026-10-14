import static kit.Check.assertEquals;
import static kit.Check.assertThrows;

import java.nio.file.Path;
import java.util.Map;
import java.util.OptionalInt;
import kit.Test;

public class Phase2Tests {

    private static HoldBook book(Hold... holds) {
        HoldBook book = new HoldBook();
        for (Hold hold : holds) {
            book.add(hold);
        }
        return book;
    }

    @Test(order = 1)
    void singleHold() {
        ExposureCalculator calculator = new ExposureCalculator(book(new Hold("a", "RIYA", 500, 7, 10)));
        assertEquals(new Peak(500, 7), calculator.peak("RIYA"));
    }

    @Test(order = 2)
    void overlappingHoldsAddUp() {
        ExposureCalculator calculator = new ExposureCalculator(book(
                new Hold("a", "RIYA", 500, 0, 10),
                new Hold("b", "RIYA", 300, 5, 15),
                new Hold("c", "RIYA", 100, 8, 9)));
        assertEquals(new Peak(900, 8), calculator.peak("RIYA"));
    }

    @Test(order = 3)
    void holdsThatOnlyTouchDoNotOverlap() {
        ExposureCalculator calculator = new ExposureCalculator(book(
                new Hold("a", "ZED", 1000, 0, 10),
                new Hold("b", "ZED", 1000, 10, 20),
                new Hold("c", "ZED", 1000, 20, 30)));
        assertEquals(new Peak(1000, 0), calculator.peak("ZED"));
    }

    @Test(order = 4)
    void earliestMinuteWinsWhenThePeakIsReachedTwice() {
        ExposureCalculator calculator = new ExposureCalculator(book(
                new Hold("a", "RIYA", 400, 50, 60),
                new Hold("b", "RIYA", 400, 5, 9),
                new Hold("c", "RIYA", 100, 0, 3)));
        assertEquals(new Peak(400, 5), calculator.peak("RIYA"));
    }

    @Test(order = 5)
    void holdsAddedOutOfOrder() {
        ExposureCalculator calculator = new ExposureCalculator(book(
                new Hold("a", "RIYA", 10, 40, 50),
                new Hold("b", "RIYA", 20, 0, 45),
                new Hold("c", "RIYA", 30, 44, 100),
                new Hold("d", "RIYA", 5, 43, 44)));
        assertEquals(new Peak(60, 44), calculator.peak("RIYA"));
    }

    @Test(order = 6)
    void releasedHoldsAndOtherAccountsAreIgnored() {
        HoldBook book = book(
                new Hold("a", "RIYA", 500, 0, 10),
                new Hold("b", "RIYA", 300, 5, 15),
                new Hold("x", "ZED", 9_999, 5, 15));
        book.release("a");
        assertEquals(new Peak(300, 5), new ExposureCalculator(book).peak("RIYA"));
    }

    @Test(order = 7)
    void accountWithoutHolds() {
        HoldBook book = book(new Hold("a", "RIYA", 500, 0, 10));
        book.release("a");
        ExposureCalculator calculator = new ExposureCalculator(book);
        assertEquals(Peak.NONE, calculator.peak("RIYA"));
        assertEquals(Peak.NONE, calculator.peak("NOBODY"));
        assertEquals(OptionalInt.empty(), calculator.firstBreach("NOBODY", 0));
    }

    @Test(order = 8)
    void firstBreachIsTheFirstMinuteAboveTheLimit() {
        ExposureCalculator calculator = new ExposureCalculator(book(
                new Hold("a", "RIYA", 500, 0, 10),
                new Hold("b", "RIYA", 300, 5, 15),
                new Hold("c", "RIYA", 100, 8, 9)));
        assertEquals(OptionalInt.of(0), calculator.firstBreach("RIYA", 499));
        assertEquals(OptionalInt.of(5), calculator.firstBreach("RIYA", 500));
        assertEquals(OptionalInt.of(5), calculator.firstBreach("RIYA", 799));
        assertEquals(OptionalInt.of(8), calculator.firstBreach("RIYA", 800));
        assertEquals(OptionalInt.empty(), calculator.firstBreach("RIYA", 900));
    }

    @Test(order = 9)
    void aHoldEndingAsAnotherStartsDoesNotBreach() {
        ExposureCalculator calculator = new ExposureCalculator(book(
                new Hold("b", "ZED", 1000, 10, 20),
                new Hold("a", "ZED", 1000, 0, 10)));
        assertEquals(OptionalInt.empty(), calculator.firstBreach("ZED", 1000));
        assertEquals(OptionalInt.of(0), calculator.firstBreach("ZED", 999));
    }

    @Test(order = 10)
    void negativeLimitIsRejected() {
        ExposureCalculator calculator = new ExposureCalculator(book(new Hold("a", "RIYA", 500, 0, 10)));
        assertThrows(IllegalArgumentException.class, () -> calculator.firstBreach("RIYA", -1));
    }

    @Test(order = 11)
    void sampleFile() throws Exception {
        ExposureCalculator calculator = new ExposureCalculator(HoldParser.fromFile(Path.of("data/holds.csv")));
        assertEquals(new Peak(255_049, 30), calculator.peak("RIYA"));
        assertEquals(new Peak(2_475_000_000L, 60), calculator.peak("ACME"));
        assertEquals(new Peak(1_000, 0), calculator.peak("ZED"));
        assertEquals(OptionalInt.of(60), calculator.firstBreach("ACME", 2_000_000_000L));
        Map<String, Peak> all = calculator.peakByAccount();
        assertEquals(3, all.size());
        assertEquals(new Peak(255_049, 30), all.get("RIYA"));
    }
}
