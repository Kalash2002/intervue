# Grader notes: Playlist Dedupe

**Phase 1 bug.** `SongKey.normalise` calls `s.toLowerCase(Locale.ROOT)` and discards the result. Strings are immutable, so the call has no effect and copies that differ only in case keep different keys. Trimming, spacing and the remaster tag work, which is why most tests pass. Fix: assign the result (`s = s.toLowerCase(Locale.ROOT)`). Strong candidates mention that static analysis (for example Error Prone's `ReturnValueIgnored`) catches this class of bug.

**Phase 2.** `topArtists` counts distinct users per normalised artist: a `Map<String, Set<String>>`. Counting plays instead of users fails `listenersAreCountedOncePerArtist` (the Weeknd has four plays from three users). Ties break by name. `similarity` is the Jaccard index over song keys, floored with integer arithmetic; duplicates inside a playlist count once, unknown ids are ignored, and two empty playlists return 0 (not a division by zero).

**Phase 3.** Two playlists of 80,000 tracks and 400,000 plays over 10 artists. List-based `contains` or `indexOf` makes both reports quadratic and both tests time out; hash sets make them linear. Caching the normalised artist per track id is a nice touch, not required.

**Common assistant mistakes to watch for.** "Fixing" phase 1 by comparing with `equalsIgnoreCase` in one place instead of fixing the key; counting plays instead of listeners; reporting the artist's original spelling so "Dua  Lipa" and "dua lipa" split; computing the percentage with doubles and rounding instead of flooring; returning 100 for two empty playlists.
