# Grader notes: Merchant Suggest

**Phase 1 bug.** `MerchantDirectory.find` is a hand-written binary search with `while (low < high)` together with `high = middle - 1`. With an inclusive `high`, the loop must run while `low <= high`; as written it stops before testing the last remaining candidate. Any merchant whose search narrows down to a single position is missed, which is why only some names fail (and a one-merchant directory always fails). Fix: `low <= high`, or use `Collections.binarySearch` as `add` already does. Cost: O(log n).

**Phase 2.** Straightforward version: normalize the typed text; for each merchant, test `key.startsWith(prefix) || key.contains(" " + prefix)`; sort by count descending then normalized name; cut to the limit. Details that catch people: the tie-break is on the normalized name, not the display name; "Domino's Pizza" normalizes to "domino s pizza" and so matches "s"; a name with a repeated word must appear once; multi-word typed text must match across a word boundary; suggestions must follow later changes to the directory.

**Phase 3.** Three separate causes:
1. Loading: `MerchantDirectory.add` inserts into two sorted array lists. The feed arrives sorted Z to A, so every insert lands at the front and shifts the whole list: the feed is quadratic: two to five minutes on the reference machine against about 2.5 s with a sorted map. The order is there so the slow path is unmistakable under any calibration factor (PLAN.md 4.4). Fix with a sorted map, or append and sort lazily. This is in existing code, not in the candidate's phase 2 code.
2. Specific searches: scanning 100,000 merchants per call is too slow. Index every word-start suffix of every normalized name in sorted order (or a trie); the matches for a prefix are then one contiguous range found by binary search.
3. Letter by letter: one- and two-letter prefixes match tens of thousands of entries and the same short prefixes are asked again and again. Remember the answer per prefix (or store the top ten at each trie node). The remembered answers and the index must be discarded when the directory changes; `suggestionsFollowChangesToTheDirectory` in phase 2 catches a stale cache, and the candidate needs some change signal on the directory (a version counter).

A candidate who does not finish but names all three causes and the trade-off (memory for the index, staleness for the cache) has shown the insight.

**Common assistant mistakes to watch for.** A trie over whole names only, which misses later words; tie-break on display name; a cache that is never invalidated; rebuilding the index on every call; checking directory size instead of a version, which misses a replaced merchant; optimising `Suggester` and never looking at `MerchantDirectory.add`.
