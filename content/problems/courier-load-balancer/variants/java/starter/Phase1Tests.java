import static kit.Check.assertEquals;
import static kit.Check.assertThrows;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import kit.Test;

public class Phase1Tests {

    @Test(order = 1)
    void oneCourierCarriesEverything() {
        Plan plan = new LoadBalancer(List.of("A")).balance(List.of(new Parcel("p1", 300), new Parcel("p2", 500)));
        assertEquals(Map.of("A", List.of("p2", "p1")), plan.parcels());
        assertEquals(800L, plan.heaviestLoad());
    }

    @Test(order = 2)
    void noParcelsMeansEmptyVans() {
        Plan plan = new LoadBalancer(List.of("A", "B")).balance(List.of());
        assertEquals(Map.of("A", 0L, "B", 0L), plan.loads());
        assertThrows(IllegalArgumentException.class, () -> new LoadBalancer(List.of()));
    }

    @Test(order = 3)
    void tiesGoToTheCourierListedFirst() {
        Plan plan = new LoadBalancer(List.of("A", "B", "C"))
                .balance(List.of(new Parcel("p1", 100), new Parcel("p2", 100), new Parcel("p3", 100)));
        assertEquals(Map.of("A", List.of("p1"), "B", List.of("p2"), "C", List.of("p3")), plan.parcels());
    }

    @Test(order = 4)
    void heaviestParcelsGoFirst() {
        Plan plan = new LoadBalancer(List.of("A", "B")).balance(List.of(
                new Parcel("p3", 3), new Parcel("p1", 5), new Parcel("p4", 3), new Parcel("p2", 4)));
        assertEquals(List.of("p1", "p4"), plan.parcels().get("A"));
        assertEquals(List.of("p2", "p3"), plan.parcels().get("B"));
    }

    @Test(order = 5)
    void sampleFile() throws Exception {
        Plan plan = new LoadBalancer(List.of("ASHA", "BILAL", "CHEN"))
                .balance(ParcelParser.fromFile(Path.of("data/parcels.csv")));
        assertEquals(List.of("P1", "P6"), plan.parcels().get("ASHA"));
        assertEquals(List.of("P2", "P4"), plan.parcels().get("BILAL"));
        assertEquals(List.of("P3", "P5"), plan.parcels().get("CHEN"));
        assertEquals(5_500L, plan.heaviestLoad());
    }
}
