import static kit.Check.assertEquals;
import static kit.Check.assertThrows;

import java.nio.file.Path;
import java.util.List;
import kit.Test;

public class Phase2Tests {

    private static PlaylistTools tools() throws Exception {
        return new PlaylistTools(CatalogueParser.fromFile(Path.of("data/catalogue.csv")));
    }

    private static final List<Play> PLAYS = List.of(
            new Play("S1", "u1"), new Play("S2", "u1"), new Play("S8", "u2"), new Play("S3", "u1"),
            new Play("S4", "u3"), new Play("S7", "u3"), new Play("S5", "u2"), new Play("S6", "u4"),
            new Play("S8", "u5"), new Play("S99", "u6"));

    @Test(order = 1)
    void listenersAreCountedOncePerArtist() throws Exception {
        assertEquals(List.of(new ArtistCount("the weeknd", 3), new ArtistCount("dua lipa", 2)),
                tools().topArtists(PLAYS, 2));
    }

    @Test(order = 2)
    void tiesAreBrokenByName() throws Exception {
        assertEquals(List.of(new ArtistCount("the weeknd", 3), new ArtistCount("dua lipa", 2),
                new ArtistCount("the beatles", 2)), tools().topArtists(PLAYS, 10));
    }

    @Test(order = 3)
    void edgeCasesOfTopArtists() throws Exception {
        PlaylistTools t = tools();
        assertEquals(List.of(), t.topArtists(PLAYS, 0));
        assertEquals(List.of(), t.topArtists(List.of(), 3));
        assertThrows(IllegalArgumentException.class, () -> t.topArtists(PLAYS, -1));
    }

    @Test(order = 4)
    void similarityCountsSongsNotTracks() throws Exception {
        PlaylistTools t = tools();
        assertEquals(50, t.similarity(List.of("S1", "S3", "S5"), List.of("S2", "S4", "S8", "S99")));
        assertEquals(100, t.similarity(List.of("S1", "S2"), List.of("S1")));
    }

    @Test(order = 5)
    void similarityRoundsDown() throws Exception {
        assertEquals(33, tools().similarity(List.of("S1", "S3", "S7"), List.of("S3")));
    }

    @Test(order = 6)
    void emptyPlaylistsShareNothing() throws Exception {
        PlaylistTools t = tools();
        assertEquals(0, t.similarity(List.of(), List.of()));
        assertEquals(0, t.similarity(List.of("S1"), List.of()));
    }
}
