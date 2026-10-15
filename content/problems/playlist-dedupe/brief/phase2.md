## Phase 2: listeners and similarity

Implement two methods in `PlaylistTools`:

```java
List<ArtistCount> topArtists(List<Play> plays, int k)
int similarity(List<String> playlistA, List<String> playlistB)
```

`topArtists` rules:

- Count, for every artist, how many **different users** played any of the artist's tracks. Playing ten tracks counts once.
- Artists are compared by their normalised name, and reported by it.
- Return the `k` artists with the most listeners, most first; break ties by name, alphabetically. Throw `IllegalArgumentException` for a negative `k`.
- Plays of track ids that are not in the catalogue are ignored.

`similarity` rules:

- Turn each playlist (a list of track ids) into its set of songs, using the song key.
- Return `100 * shared / all`, rounded down, where `shared` is the number of songs in both and `all` the number of songs in either.
- Two empty playlists have similarity 0. Unknown track ids are ignored.

Run **Phase 2 tests**.
