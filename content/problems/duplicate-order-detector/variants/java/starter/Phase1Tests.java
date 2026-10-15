import static kit.Check.assertEquals;

import java.nio.file.Path;
import java.util.List;
import kit.Test;

public class Phase1Tests {

    private static DuplicateDetector detector(Order... orders) {
        OrderLog log = new OrderLog();
        for (Order order : orders) {
            log.add(order);
        }
        return new DuplicateDetector(log);
    }

    @Test(order = 1)
    void differentBasketsAreNotRepeats() {
        DuplicateDetector d = detector(
                new Order("a", "RIYA", "SKU-1", 500, 0),
                new Order("b", "RIYA", "SKU-2", 500, 1),
                new Order("c", "ZED", "SKU-1", 500, 2));
        assertEquals(List.of(), d.exactRepeats());
        assertEquals(3, d.distinctBaskets());
    }

    @Test(order = 2)
    void sameBasketTwiceIsARepeat() {
        DuplicateDetector d = detector(
                new Order("a", "RIYA", "SKU-1", 500, 0),
                new Order("b", "RIYA", "SKU-1", 500, 5));
        assertEquals(List.of("b"), d.exactRepeats());
        assertEquals(1, d.distinctBaskets());
    }

    @Test(order = 3)
    void aDifferentAmountIsADifferentBasket() {
        DuplicateDetector d = detector(
                new Order("a", "RIYA", "SKU-1", 500, 0),
                new Order("b", "RIYA", "SKU-1", 501, 5));
        assertEquals(List.of(), d.exactRepeats());
    }

    @Test(order = 4)
    void everyLaterCopyIsARepeat() {
        DuplicateDetector d = detector(
                new Order("a", "RIYA", "SKU-1", 500, 0),
                new Order("b", "ZED", "SKU-1", 500, 1),
                new Order("c", "RIYA", "SKU-1", 500, 2),
                new Order("d", "RIYA", "SKU-1", 500, 9));
        assertEquals(List.of("c", "d"), d.exactRepeats());
        assertEquals(2, d.distinctBaskets());
    }

    @Test(order = 5)
    void sampleFile() throws Exception {
        DuplicateDetector d = new DuplicateDetector(OrderParser.fromFile(Path.of("data/orders.csv")));
        assertEquals(List.of("O3", "O6", "O8", "O9", "O10"), d.exactRepeats());
        assertEquals(5, d.distinctBaskets());
    }
}
