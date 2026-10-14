import java.nio.file.Path;

/** Small demo. Run it from the Run menu to see the sample playlist before and after dedupe. */
public class Main {
    public static void main(String[] args) throws Exception {
        Catalogue catalogue = CatalogueParser.fromFile(Path.of("data/catalogue.csv"));
        PlaylistTools tools = new PlaylistTools(catalogue);
        System.out.println("Before: " + catalogue.ids());
        System.out.println("After:  " + tools.dedupe(catalogue.ids()));
        for (String id : catalogue.ids()) {
            Track t = catalogue.find(id).orElseThrow();
            System.out.println(id + "  [" + t.title() + "] by [" + t.artist() + "] -> " + SongKey.normalise(t.title()));
        }
    }
}
