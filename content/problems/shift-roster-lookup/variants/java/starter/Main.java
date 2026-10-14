import java.nio.file.Path;

/** Small demo. Run it from the Run menu to see lookups on the sample roster. */
public class Main {
    public static void main(String[] args) throws Exception {
        RosterLookup lookup = new RosterLookup(RosterParser.fromFile(Path.of("data/roster.csv")));
        for (String ward : new String[] {"ICU", "WARD-2"}) {
            for (int minute : new int[] {0, 100, 480, 1000, 1499, 1500}) {
                System.out.println(ward + " at minute " + minute + ": " + lookup.onDutyAt(ward, minute).orElse("nobody"));
            }
        }
    }
}
