# How a strong round goes: Playlist Dedupe

**Read this first.** This is one good way through the problem, not the only one. The complexity figures describe the reference solution. The practice links go to other sites and to different questions that use the same technique; they are not this question. The prompts are written for interview mode, where the assistant does what you specify and nothing more.

## Minutes 0 to 6: read before you run

Say aloud: "I'll read the key, the catalogue and the tools, and the sample, before I run anything."

1. `SongKey.normalise`: four steps, strip, lower case, collapse spaces, drop a "(...remaster...)" suffix. Read each line and say what it returns and where the result goes.
2. `SongKey.of`: the normalised title and artist joined by a separator; equal keys mean the same song.
3. `Catalogue`: tracks by id in a hash map, plus the import order.
4. `PlaylistTools.dedupe`: a set of seen keys, first copy wins. It is already linear; note it as the pattern for phase 2.
5. `data/catalogue.csv`: eight tracks. S2, S4 and S6 are copies of S1, S3 and S5; S7 is a live version and must stay.

Only now run the phase 1 tests.

## Prompt: understand the codebase fast

```text
Read-only question. For this project give me:
1. One line per class: what it owns and which other classes it calls.
2. For SongKey.normalise: for each line, the value of s after that line for the input "  LEVITATING ".
3. Which methods of PlaylistTools already exist and what data structure each one uses.
Keep it under 150 words. Mark anything you are inferring.
```

Check item 2 yourself on paper. An assistant that says the second line turns s into "levitating" is reading intent, not code.

## Phase 1: capital letters survive (minutes 6 to 12)

### Reproduce

Run phase 1 tests. `trimsAndCollapsesSpaces`, `dropsARemasterTag` and `dedupeKeepsOrderAndDropsUnknownIds` pass. `ignoresCase` fails with "LEVITATING", and `sampleCatalogue` keeps S6 as a separate song.

### Narrow it down, aloud

"Spacing and the remaster tag work, so the pipeline runs. Only lower-casing has no effect. `toLowerCase` returns a new string; strings never change in place, and this line throws the new one away."

### The cause

```java
s.toLowerCase(Locale.ROOT);
```

### The fix

```java
s = s.toLowerCase(Locale.ROOT);
```

Run phase 1 tests again; all five pass.

### Say this

"An ignored return value is legal Java, so it compiled and ran. Static analysis, for example Error Prone's `ReturnValueIgnored`, flags exactly this. The remaster tag still worked because its pattern is case-insensitive."

## Phase 2: listeners and similarity (minutes 12 to 38)

### Analysis before any prompt

- Listeners are **distinct users** per **normalised** artist: a map from artist to a set of users. Counting plays is wrong; the Weeknd has four plays from three users in the test.
- Similarity is shared songs over all songs, the Jaccard index, rounded down with integer arithmetic. Songs, not track ids, so both playlists become sets of song keys first. Two empty playlists give 0, not a division by zero.

Work the sample by hand: A = S1, S3, S5 is three songs. B = S2, S4, S8, S99 is S2 (same as S1), S4 (same as S3), S8, and an unknown id. Shared 2, all 4, so 50.

### Decide the approach yourself

"Both reports are one pass with hash collections, like `dedupe`. The normalised artist is `SongKey.normalise(track.artist())`."

### Prompt

```text
Implement PlaylistTools.topArtists(plays, k): throw IllegalArgumentException for k < 0. For each
play, find the track in the catalogue and skip unknown ids; add play.user() to a
HashMap<String, Set<String>> keyed by SongKey.normalise(track.artist()). Return ArtistCount
(artist, set size) sorted by listeners descending, then artist ascending, limited to k.
Do not change other methods.
```

Then:

```text
Implement similarity(playlistA, playlistB): turn each list of track ids into a HashSet of
track.songKey(), skipping unknown ids. all = size of the union, shared = size of the
intersection. Return 0 when all is 0, else 100 * shared / all with integer division.
```

### Check the reply for

- Users in a set, not a counter.
- The artist normalised, so "Dua  Lipa" and "dua lipa" are one artist.
- A comparator with the name as tie-break.
- Integer division, not `Math.round` on a double. `similarityRoundsDown` expects 33 for one shared song out of three.
- Copies of the input sets before `retainAll` or `addAll`, or the union is computed from an already-shrunk set.

Run phase 2 tests after each method.

## Phase 3: a busy day (minutes 38 to 50)

### Read the tests first

Two playlists of 80,000 tracks, and 400,000 plays from 100,000 users. Say how many times each method touches each item: "Once, with a hash lookup each. A `List.contains` anywhere in the loop makes it 80,000 times 80,000."

### What to do

If phase 2 used hash sets and a map as above, phase 3 already passes. If either test times out, find the list used as a set (`contains`, `indexOf`, or a list of artists searched by name) and replace it with a `HashSet` or a `HashMap`. Run all three phases.

## The technique: normalised keys in hash sets

**The idea.** Decide once what makes two things "the same", write that as a function from the thing to a key, and let hash sets and maps do the comparing. Deduplication, distinct counts and set similarity all become one pass.

**How to recognise it.** "Same song in different spellings", group anagrams, unique emails after removing dots, distinct visitors, overlap between two lists.

**The two decisions that cause bugs.** The normalising function is the single source of truth: fix "same song" there, never with an `equalsIgnoreCase` in one report. And strings are immutable: every `String` method returns a new string, so its result must be used.

**Set similarity.** Shared over all is the Jaccard index. With hash sets it is linear; with lists it is quadratic. For huge sets, MinHash estimates it from small signatures.

| Approach | Time for playlists of n and m tracks | Extra space |
| --- | --- | --- |
| Lists with `contains` | O(n x m) | O(n + m) |
| Hash sets of song keys | O(n + m) | O(n + m) |
| Sort both key lists and merge | O(n log n + m log m) | O(n + m) |

**Practise the technique**

These open inside the app under DSA.

- [Group Anagrams](dsa://group-anagrams): a normalised key per string in a hash map.
- [Unique Email Addresses](dsa://unique-email-addresses): normalising rules, then a set.
- [Intersection of Two Arrays](dsa://intersection-of-two-arrays): the shared part of `similarity`.

## Time budget

| Minutes | What |
| --- | --- |
| 0 to 6 | Read the key, the tools, the sample and the test names |
| 6 to 12 | Phase 1: reproduce, trace `normalise` line by line, assign the result |
| 12 to 16 | Phase 2 rules aloud and the sample similarity by hand |
| 16 to 38 | Phase 2: `topArtists`, then `similarity`, a run after each |
| 38 to 50 | Phase 3: read the tests, count touches per item, run, replace any list lookup |
| 50 to 60 | Read your whole diff, answer the follow-ups aloud |

## Follow-up answers

**Why did it compile and run?** `toLowerCase` returns a new string and Java lets you ignore any return value. The compiler checks types, not intent. Nothing failed at run time because nothing was wrong as far as Java could tell.

**Other methods with the same trap.** Every `String` method that "changes" text: `strip`, `trim`, `replace`, `replaceAll`, `concat`, `substring`, `toUpperCase`. The same holds for `BigDecimal`, `LocalDate` and other immutable types (`date.plusDays(1)` alone does nothing). In review, look for a call on an immutable value whose result is not assigned; tools such as Error Prone flag it.

**Cost.** `similarity` is O(n + m) with hash sets. `topArtists` is O(p) for p plays, plus sorting the artists, which is small.

**"Live" different, "Remastered" the same.** The rule lives in `SongKey`, the one place that defines "same song", as a pattern for the suffixes that do not change the song. Test it there with pairs that must and must not collide: "Song (Remastered 2009)" equals "Song", "Song (Live)" does not, and a title that merely contains the word "remaster" is not stripped.

**What the assistant got wrong.** Answer from your own session. Typical: `equalsIgnoreCase` in one place instead of fixing the key, counting plays instead of listeners, reporting the original spelling of the artist, rounding with doubles instead of flooring, or 100 for two empty playlists.
