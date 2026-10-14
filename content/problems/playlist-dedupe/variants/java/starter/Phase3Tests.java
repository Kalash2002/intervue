import static kit.Check.assertEquals;

import java.util.List;
import kit.Test;

/** Large catalogues. Each test has a time limit; a correct but slow solution fails with TIMEOUT. */
public class Phase3Tests {

    /** Two playlists of 80,000 tracks from a 100,000 track catalogue. */
    @Test(order = 1, timeoutMs = 6000)
    void similarityOfTwoBigPlaylists() {
        Catalogue catalogue = Datasets.catalogue(100_000, 5_000, 21L);
        PlaylistTools tools = new PlaylistTools(catalogue);
        int score = tools.similarity(
                Datasets.playlist(catalogue, 80_000, 22L), Datasets.playlist(catalogue, 80_000, 23L));
        assertEquals(EXPECTED_SIMILARITY, score);
    }

    /** 400,000 plays by 100,000 users across 10 artists. */
    @Test(order = 2, timeoutMs = 4000)
    void topArtistsOverABusyDay() {
        Catalogue catalogue = Datasets.catalogue(20_000, 10, 31L);
        List<ArtistCount> top = new PlaylistTools(catalogue)
                .topArtists(Datasets.plays(catalogue, 400_000, 100_000, 32L), 3);
        assertEquals(EXPECTED_TOP, top);
    }

    static final int EXPECTED_SIMILARITY = 45;
    static final List<ArtistCount> EXPECTED_TOP = List.of(new ArtistCount("artist 2", 34_047),
            new ArtistCount("artist 6", 33_466), new ArtistCount("artist 1", 33_241));
}
