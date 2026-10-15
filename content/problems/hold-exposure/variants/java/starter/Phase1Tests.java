import static kit.Check.assertEquals;
import static kit.Check.assertFalse;
import static kit.Check.assertTrue;

import java.nio.file.Path;
import kit.Test;

public class Phase1Tests {

    @Test(order = 1)
    void sumsOverlappingHolds() {
        HoldBook book = new HoldBook();
        book.add(new Hold("a", "RIYA", 500, 0, 10));
        book.add(new Hold("b", "RIYA", 300, 5, 15));
        ExposureCalculator calculator = new ExposureCalculator(book);
        assertEquals(500, calculator.exposureAt("RIYA", 0));
        assertEquals(800, calculator.exposureAt("RIYA", 5));
        assertEquals(300, calculator.exposureAt("RIYA", 12));
    }

    @Test(order = 2)
    void holdEndsBeforeItsEndMinute() {
        HoldBook book = new HoldBook();
        book.add(new Hold("a", "RIYA", 500, 0, 10));
        ExposureCalculator calculator = new ExposureCalculator(book);
        assertEquals(500, calculator.exposureAt("RIYA", 9));
        assertEquals(0, calculator.exposureAt("RIYA", 10));
    }

    @Test(order = 3)
    void otherAccountsAreIgnored() {
        HoldBook book = new HoldBook();
        book.add(new Hold("a", "RIYA", 500, 0, 10));
        book.add(new Hold("b", "ZED", 900, 0, 10));
        assertEquals(500, new ExposureCalculator(book).exposureAt("RIYA", 3));
        assertEquals(0, new ExposureCalculator(book).exposureAt("NOBODY", 3));
    }

    @Test(order = 4)
    void releasedHoldsAreIgnored() {
        HoldBook book = new HoldBook();
        book.add(new Hold("a", "RIYA", 500, 0, 10));
        book.add(new Hold("b", "RIYA", 300, 0, 10));
        assertTrue(book.release("a"), "release of a known hold");
        assertFalse(book.release("nope"), "release of an unknown hold");
        assertEquals(300, new ExposureCalculator(book).exposureAt("RIYA", 3));
    }

    @Test(order = 5)
    void sampleFileRetailAccount() throws Exception {
        HoldBook book = HoldParser.fromFile(Path.of("data/holds.csv"));
        ExposureCalculator calculator = new ExposureCalculator(book);
        assertEquals(120_000 + 35_050 + 99_999, calculator.exposureAt("RIYA", 30));
        assertEquals(8_000, calculator.exposureAt("RIYA", 60));
    }

    @Test(order = 6)
    void sampleFileCorporateAccount() throws Exception {
        HoldBook book = HoldParser.fromFile(Path.of("data/holds.csv"));
        ExposureCalculator calculator = new ExposureCalculator(book);
        assertEquals(900_000_000L, calculator.exposureAt("ACME", 0), "minute 0");
        assertEquals(1_750_000_000L, calculator.exposureAt("ACME", 30), "minute 30");
        assertEquals(2_475_000_000L, calculator.exposureAt("ACME", 60), "minute 60");
    }

    @Test(order = 7)
    void treasuryAccountWithVeryLargeHolds() {
        HoldBook book = new HoldBook();
        for (int i = 0; i < 40; i++) {
            book.add(new Hold("t" + i, "TREASURY", 2_000_000_000L, 0, 100));
        }
        assertEquals(80_000_000_000L, new ExposureCalculator(book).exposureAt("TREASURY", 50));
    }
}
