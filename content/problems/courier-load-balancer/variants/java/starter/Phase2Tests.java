import static kit.Check.assertEquals;
import static kit.Check.assertThrows;

import java.util.List;
import kit.Test;

public class Phase2Tests {

    private static final LoadBalancer TWO = new LoadBalancer(List.of("A", "B"));

    @Test(order = 1)
    void parcelsThatDoNotFitAreLeftOver() {
        Plan plan = TWO.balanceWithCapacity(List.of(
                new Parcel("p8", 8), new Parcel("p7", 7), new Parcel("p5", 5), new Parcel("p3", 3)), 10);
        assertEquals(List.of("p8"), plan.parcels().get("A"));
        assertEquals(List.of("p7", "p3"), plan.parcels().get("B"));
        assertEquals(List.of("p5"), plan.leftOver());
    }

    @Test(order = 2)
    void aParcelExactlyAtCapacityFits() {
        Plan plan = TWO.balanceWithCapacity(List.of(new Parcel("p", 10)), 10);
        assertEquals(List.of("p"), plan.parcels().get("A"));
        assertEquals(List.of(), plan.leftOver());
    }

    @Test(order = 3)
    void aParcelHeavierThanAnyVanIsLeftOver() {
        Plan plan = TWO.balanceWithCapacity(List.of(new Parcel("big", 11), new Parcel("small", 2)), 10);
        assertEquals(List.of("big"), plan.leftOver());
        assertEquals(List.of("small"), plan.parcels().get("A"));
    }

    @Test(order = 4)
    void leftOversKeepTheOrderTheyWereOffered() {
        Plan plan = new LoadBalancer(List.of("A")).balanceWithCapacity(List.of(
                new Parcel("b", 6), new Parcel("a", 6), new Parcel("c", 9)), 10);
        assertEquals(List.of("c"), plan.parcels().get("A"));
        assertEquals(List.of("a", "b"), plan.leftOver());
    }

    @Test(order = 5)
    void withRoomToSpareItMatchesBalance() {
        List<Parcel> parcels = List.of(new Parcel("p1", 5), new Parcel("p2", 4), new Parcel("p3", 3), new Parcel("p4", 3));
        assertEquals(TWO.balance(parcels).parcels(), TWO.balanceWithCapacity(parcels, 1_000).parcels());
    }

    @Test(order = 6)
    void capacityMustBePositive() {
        assertThrows(IllegalArgumentException.class, () -> TWO.balanceWithCapacity(List.of(), 0));
        assertThrows(IllegalArgumentException.class, () -> TWO.balanceWithCapacity(List.of(), -5));
    }
}
