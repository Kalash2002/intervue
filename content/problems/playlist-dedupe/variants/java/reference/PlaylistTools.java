import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/** Playlist operations over a catalogue. Every report is one pass with hash sets. */
public class PlaylistTools {
    private final Catalogue catalogue;

    public PlaylistTools(Catalogue catalogue) {
        this.catalogue = catalogue;
    }

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

    public List<ArtistCount> topArtists(List<Play> plays, int k) {
        if (k < 0) {
            throw new IllegalArgumentException("k cannot be negative: " + k);
        }
        Map<String, Set<String>> listeners = new HashMap<>();
        Map<String, String> artistOfTrack = new HashMap<>();
        for (Play play : plays) {
            String artist = artistOfTrack.computeIfAbsent(play.trackId(),
                    id -> catalogue.find(id).map(t -> SongKey.normalise(t.artist())).orElse(null));
            if (artist != null) {
                listeners.computeIfAbsent(artist, a -> new HashSet<>()).add(play.user());
            }
        }
        return listeners.entrySet().stream()
                .map(e -> new ArtistCount(e.getKey(), e.getValue().size()))
                .sorted(Comparator.comparingInt(ArtistCount::listeners).reversed().thenComparing(ArtistCount::artist))
                .limit(k)
                .toList();
    }

    public int similarity(List<String> playlistA, List<String> playlistB) {
        Set<String> a = songs(playlistA);
        Set<String> b = songs(playlistB);
        int shared = 0;
        for (String song : a) {
            if (b.contains(song)) {
                shared++;
            }
        }
        int all = a.size() + b.size() - shared;
        return all == 0 ? 0 : (int) (100L * shared / all);
    }

    private Set<String> songs(List<String> playlist) {
        Set<String> songs = new HashSet<>();
        for (String id : playlist) {
            catalogue.find(id).ifPresent(t -> songs.add(t.songKey()));
        }
        return songs;
    }
}
