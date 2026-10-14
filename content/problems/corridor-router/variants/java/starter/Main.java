import java.nio.file.Path;

/** Small demo. Run it from the Run menu to see the router on the sample network. */
public class Main {
    public static void main(String[] args) throws Exception {
        Network network = NetworkLoader.fromFile(Path.of("data/corridors.txt"));
        RouteFinder finder = new RouteFinder(network);
        System.out.println(network.nodes().size() + " nodes, " + network.corridorCount() + " corridors");
        print("fewest hops INR -> USD", finder.fewestHops("INR", "USD").orElse(null));
        print("fewest hops INR -> BRL", finder.fewestHops("INR", "BRL").orElse(null));
        print("fewest hops USD -> INR", finder.fewestHops("USD", "INR").orElse(null));
    }

    private static void print(String label, Route route) {
        if (route == null) {
            System.out.println(label + ": no route");
        } else {
            System.out.println(label + ": " + String.join(" > ", route.stops()) + "  fee " + route.totalFeeMinor());
        }
    }
}
