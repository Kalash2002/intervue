import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Naive solution: correct, and slow on purpose. Songs and listeners live in lists searched with
 * contains, so similarity is O(n * m) and topArtists is O(plays * listeners). Phases 1 and 2 pass;
 * phase 3 must time out.
 */
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
        List<String> artists = new ArrayList<>();
        List<List<String>> listeners = new ArrayList<>();
        for (Play play : plays) {
            Optional<Track> track = catalogue.find(play.trackId());
            if (track.isEmpty()) {
                continue;
            }
            String artist = SongKey.normalise(track.get().artist());
            int at = artists.indexOf(artist);
            if (at < 0) {
                artists.add(artist);
                listeners.add(new ArrayList<>());
                at = artists.size() - 1;
            }
            if (!listeners.get(at).contains(play.user())) {
                listeners.get(at).add(play.user());
            }
        }
        List<ArtistCount> counts = new ArrayList<>();
        for (int i = 0; i < artists.size(); i++) {
            counts.add(new ArtistCount(artists.get(i), listeners.get(i).size()));
        }
        counts.sort((x, y) -> x.listeners() != y.listeners()
                ? y.listeners() - x.listeners() : x.artist().compareTo(y.artist()));
        return counts.subList(0, Math.min(k, counts.size()));
    }

    public int similarity(List<String> playlistA, List<String> playlistB) {
        List<String> a = songs(playlistA);
        List<String> b = songs(playlistB);
        int shared = 0;
        for (String song : a) {
            if (b.contains(song)) {
                shared++;
            }
        }
        int all = a.size() + b.size() - shared;
        return all == 0 ? 0 : (int) (100L * shared / all);
    }

    private List<String> songs(List<String> playlist) {
        List<String> songs = new ArrayList<>();
        for (String id : playlist) {
            Optional<Track> track = catalogue.find(id);
            if (track.isPresent() && !songs.contains(track.get().songKey())) {
                songs.add(track.get().songKey());
            }
        }
        return songs;
    }
}
