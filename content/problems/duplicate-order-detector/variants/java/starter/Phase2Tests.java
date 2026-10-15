import static kit.Check.assertEquals;
import static kit.Check.assertThrows;

import java.nio.file.Path;
import java.util.List;
import kit.Test;

public class Phase2Tests {

    private static DuplicateDetector detector(Order... orders) {
        OrderLog log = new OrderLog();
        for (Order order : orders) {
            log.add(order);
        }
        return new DuplicateDetector(log);
    }

    @Test(order = 1)
    void aRepeatInsideTheWindowIsSuspected() {
        DuplicateDetector d = detector(
                new Order("a", "RIYA", "SKU-1", 500, 0),
                new Order("b", "RIYA", "SKU-1", 500, 10));
        assertEquals(List.of("b"), d.suspectedDuplicates(15));
        assertEquals(List.of("b"), d.suspectedDuplicates(11));
    }

    @Test(order = 2)
    void exactlyTheWindowApartIsNotSuspected() {
        DuplicateDetector d = detector(
                new Order("a", "RIYA", "SKU-1", 500, 0),
                new Order("b", "RIYA", "SKU-1", 500, 10));
        assertEquals(List.of(), d.suspectedDuplicates(10));
    }

    @Test(order = 3)
    void comparesWithTheMostRecentCopy() {
        DuplicateDetector d = detector(
                new Order("a", "RIYA", "SKU-1", 500, 0),
                new Order("b", "RIYA", "SKU-1", 500, 50),
                new Order("c", "RIYA", "SKU-1", 500, 55));
        assertEquals(List.of("c"), d.suspectedDuplicates(10));
    }

    @Test(order = 4)
    void otherBasketsInBetweenDoNotMatter() {
        DuplicateDetector d = detector(
                new Order("a", "RIYA", "SKU-1", 500, 0),
                new Order("b", "ZED", "SKU-1", 500, 1),
                new Order("c", "RIYA", "SKU-2", 500, 2),
                new Order("d", "RIYA", "SKU-1", 500, 3));
        assertEquals(List.of("d"), d.suspectedDuplicates(5));
    }

    @Test(order = 5)
    void theWindowMustBePositive() {
        DuplicateDetector d = detector(new Order("a", "RIYA", "SKU-1", 500, 0));
        assertThrows(IllegalArgumentException.class, () -> d.suspectedDuplicates(0));
        assertThrows(IllegalArgumentException.class, () -> d.suspectedDuplicates(-3));
    }

    @Test(order = 6)
    void sampleFileSuspects() throws Exception {
        DuplicateDetector d = new DuplicateDetector(OrderParser.fromFile(Path.of("data/orders.csv")));
        assertEquals(List.of("O3", "O8"), d.suspectedDuplicates(15));
        assertEquals(List.of("O3", "O6", "O8"), d.suspectedDuplicates(31));
    }

    @Test(order = 7)
    void mostDuplicatedSkusRanksByCountThenName() throws Exception {
        DuplicateDetector d = new DuplicateDetector(OrderParser.fromFile(Path.of("data/orders.csv")));
        assertEquals(List.of(new SkuCount("SKU-KETTLE", 2), new SkuCount("SKU-MUG", 2)), d.mostDuplicatedSkus(2));
        assertEquals(List.of(new SkuCount("SKU-KETTLE", 2), new SkuCount("SKU-MUG", 2), new SkuCount("SKU-LAMP", 1)),
                d.mostDuplicatedSkus(10));
        assertEquals(List.of(), d.mostDuplicatedSkus(0));
    }

    @Test(order = 8)
    void skusWithoutRepeatsAreLeftOut() {
        DuplicateDetector d = detector(
                new Order("a", "RIYA", "SKU-1", 500, 0),
                new Order("b", "RIYA", "SKU-2", 500, 1));
        assertEquals(List.of(), d.mostDuplicatedSkus(5));
        assertThrows(IllegalArgumentException.class, () -> d.mostDuplicatedSkus(-1));
    }
}
