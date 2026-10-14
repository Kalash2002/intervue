import java.nio.file.Path;

/** Small demo. Run it from the Run menu to see exposure on the sample data. */
public class Main {
    public static void main(String[] args) throws Exception {
        HoldBook book = HoldParser.fromFile(Path.of("data/holds.csv"));
        ExposureCalculator calculator = new ExposureCalculator(book);
        System.out.println(book.size() + " holds on " + book.accounts().size() + " accounts");
        for (String account : book.accounts()) {
            for (int minute : new int[] {0, 30, 60, 90}) {
                System.out.println(account + " at minute " + minute + ": " + calculator.exposureAt(account, minute) + " paise held");
            }
        }
    }
}
