# How a strong round goes: Playlist Dedupe

**Read this first.** This is one good way through the problem, not the only one. The prompts are written for interview mode, where the assistant does what you specify and nothing more.

## Minutes 0 to 6: read before you run

1. `SongKey`: four normalising steps and a key made of title and artist. Read each step and say what it returns.
2. `Track.songKey`, `Catalogue`: tracks by id; lookups are a hash map.
3. `PlaylistTools.dedupe`: a set of seen keys, first copy wins. It is already linear.
4. `data/catalogue.csv`: eight tracks; S2, S4 and S6 are copies of S1, S3 and S5; S7 is a live version and stays.

## Phase 1: capital letters survive (minutes 6 to 12)

Run phase 1. Spacing and the remaster tag pass; `ignoresCase` fails with "LEVITATING". Say: "The lower-casing step has no effect. `toLowerCase` returns a new string and this line throws it away." Assign the result and rerun. Mention that a linter would flag an ignored return value.

## Phase 2: listeners and similarity (minutes 12 to 40)

State both rules before coding: "Listeners are distinct users per normalised artist. Similarity is shared songs over all songs, floored, with 0 for two empty playlists."

```text
Implement PlaylistTools.topArtists(plays, k) with a HashMap<String, Set<String>> from the
normalised artist (SongKey.normalise) to its users. Skip plays whose track id is unknown.
Sort by listener count descending then artist ascending, limit k, and throw
IllegalArgumentException for k < 0. Do not change other methods.
```

Check the reply: users go into a set, the artist is normalised, and the comparator breaks ties by name. Then write `similarity` with two sets of song keys; work the sample by hand (shared 2, all 4, so 50) before running.

## Phase 3: a busy day (minutes 40 to 50)

Run phase 3. If any lookup in a loop goes through a list, both tests time out. Replace each list used as a set with a `HashSet`, and the artist list with a map.

## Complexity

`similarity` is O(n + m) with hash sets; `topArtists` is O(plays) plus a sort over artists.

## Follow-up answers

- An ignored return value is legal Java; the compiler checks types, not intent.
- `strip`, `trim`, `replace`, `replaceAll`, `concat` and `substring` all return new strings; `BigDecimal` and `LocalDate` methods behave the same way.
- Keep the "Live versus Remastered" rule in `SongKey` with its own tests; never sprinkle it into the reports.
