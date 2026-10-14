import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/** Builds the large catalogues, playlists and play logs used by the phase 3 tests. Same seed, same data. */
public final class Datasets {
    private Datasets() {}

    /** Tracks T0..T(n-1); about one in four repeats an earlier song with different case or spacing. */
    public static Catalogue catalogue(int tracks, int artists, long seed) {
        Random random = new Random(seed);
        Catalogue catalogue = new Catalogue();
        for (int i = 0; i < tracks; i++) {
            int song = random.nextInt(4) == 0 && i > 0 ? random.nextInt(i) : i;
            String title = "Song " + song;
            String artist = "Artist " + (song % artists);
            if (random.nextBoolean()) {
                title = title.toUpperCase();
            }
            if (random.nextBoolean()) {
                artist = " " + artist.replace(" ", "  ");
            }
            catalogue.add(new Track("T" + i, title, artist, 120 + random.nextInt(240)));
        }
        return catalogue;
    }

    /** A playlist of random track ids from the catalogue. */
    public static List<String> playlist(Catalogue catalogue, int length, long seed) {
        Random random = new Random(seed);
        List<String> playlist = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            playlist.add("T" + random.nextInt(catalogue.size()));
        }
        return playlist;
    }

    /** Plays of random tracks by random users. */
    public static List<Play> plays(Catalogue catalogue, int plays, int users, long seed) {
        Random random = new Random(seed);
        List<Play> log = new ArrayList<>(plays);
        for (int i = 0; i < plays; i++) {
            log.add(new Play("T" + random.nextInt(catalogue.size()), "U" + random.nextInt(users)));
        }
        return log;
    }
}
