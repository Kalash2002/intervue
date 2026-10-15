import static kit.Check.assertEquals;

import java.nio.file.Path;
import java.util.List;
import kit.Test;

public class Phase1Tests {

    @Test(order = 1)
    void trimsAndCollapsesSpaces() {
        assertEquals("hello world", SongKey.normalise("  hello   world "));
    }

    @Test(order = 2)
    void dropsARemasterTag() {
        assertEquals("here comes the sun", SongKey.normalise("here comes the sun (Remastered 2009)"));
        assertEquals("song (live)", SongKey.normalise("song (live)"));
    }

    @Test(order = 3)
    void ignoresCase() {
        assertEquals("levitating", SongKey.normalise("LEVITATING"));
        assertEquals("dua lipa", SongKey.normalise("  Dua   Lipa "));
        assertEquals(SongKey.of("Starboy", "The Weeknd"), SongKey.of("STARBOY", "the weeknd"));
    }

    @Test(order = 4)
    void dedupeKeepsOrderAndDropsUnknownIds() throws Exception {
        PlaylistTools tools = new PlaylistTools(CatalogueParser.fromFile(Path.of("data/catalogue.csv")));
        assertEquals(List.of("S8", "S3"), tools.dedupe(List.of("S8", "S8", "S99", "S3")));
    }

    @Test(order = 5)
    void sampleCatalogue() throws Exception {
        Catalogue catalogue = CatalogueParser.fromFile(Path.of("data/catalogue.csv"));
        assertEquals(List.of("S1", "S3", "S5", "S7", "S8"), new PlaylistTools(catalogue).dedupe(catalogue.ids()));
    }
}
