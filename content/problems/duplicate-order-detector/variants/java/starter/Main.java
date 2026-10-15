import java.nio.file.Path;

/** Small demo. Run it from the Run menu to see the report on the sample data. */
public class Main {
    public static void main(String[] args) throws Exception {
        OrderLog log = OrderParser.fromFile(Path.of("data/orders.csv"));
        DuplicateDetector detector = new DuplicateDetector(log);
        System.out.println(log.size() + " orders, " + detector.distinctBaskets() + " different baskets");
        System.out.println("Repeat orders: " + detector.exactRepeats());
    }
}
