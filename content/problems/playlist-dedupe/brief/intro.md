# Playlist Dedupe

A streaming app imports playlists from other services. The same song often arrives several times with small differences: `Blinding Lights` and `blinding lights `, or `Here Comes the Sun (Remastered 2009)`. Listeners see duplicates and complain.

Two tracks are **the same song** when their normalised title and normalised artist are equal. Normalising means: trim the ends, make it lower case, turn every run of spaces into one space, and drop a trailing part in brackets that mentions "remaster" (in any case).

You have 60 minutes for three phases. Start by reading the code; `Main` prints the sample playlist from `data/catalogue.csv` before and after dedupe.

| File | What it holds |
| --- | --- |
| `Track`, `Play`, `ArtistCount` | Data records |
| `SongKey` | Normalising and the "same song" key |
| `Catalogue` | Every track by id |
| `CatalogueParser` | Reads tracks from CSV |
| `PlaylistTools` | Dedupe and the new reports. Most of your work is here. |
| `Datasets` | Builds the large catalogues and play logs for phase 3 |

Tests are read-only. You can add your own files.
