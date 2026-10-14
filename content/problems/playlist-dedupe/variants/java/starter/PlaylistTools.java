import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/** Playlist operations over a catalogue. */
public class PlaylistTools {
    private final Catalogue catalogue;

    public PlaylistTools(Catalogue catalogue) {
        this.catalogue = catalogue;
    }

    /** The playlist without repeated songs: the first copy of each song stays, in order. Unknown ids are dropped. */
    public List<String> dedupe(List<String> playlist) {
        Set<String> seen = new HashSet<>();
        List<String> kept = new ArrayList<>();
        for (String id : playlist) {
            Optional<Track> track = catalogue.find(id);
            if (track.isPresent() && seen.add(track.get().songKey())) {
                kept.add(id);
            }
        }
        return kept;
    }

    /** Artists with the most different listeners. See the brief. */
    public List<ArtistCount> topArtists(List<Play> plays, int k) {
        throw new UnsupportedOperationException("topArtists is not implemented yet");
    }

    /** Percentage of songs two playlists share. See the brief. */
    public int similarity(List<String> playlistA, List<String> playlistB) {
        throw new UnsupportedOperationException("similarity is not implemented yet");
    }
}
