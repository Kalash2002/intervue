/** One track in the catalogue, as the import delivered it. */
public record Track(String id, String title, String artist, int durationSec) {
    public Track {
        if (title == null || artist == null) {
            throw new IllegalArgumentException("track " + id + " needs a title and an artist");
        }
    }

    public String songKey() {
        return SongKey.of(title, artist);
    }
}
