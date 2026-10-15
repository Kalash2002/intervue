import java.nio.file.Path;
import java.util.List;

/** Small demo. Run it from the Run menu to see the plan for the sample parcels. */
public class Main {
    public static void main(String[] args) throws Exception {
        List<Parcel> parcels = ParcelParser.fromFile(Path.of("data/parcels.csv"));
        LoadBalancer balancer = new LoadBalancer(List.of("ASHA", "BILAL", "CHEN"));
        Plan plan = balancer.balance(parcels);
        System.out.println(parcels.size() + " parcels");
        plan.parcels().forEach((courier, ids) ->
                System.out.println(courier + " carries " + ids + ", " + plan.loads().get(courier) + " g"));
        System.out.println("Heaviest load: " + plan.heaviestLoad() + " g");
    }
}
