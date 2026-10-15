## Level 1

Look for any `contains` or `indexOf` on a list inside a loop. On a list, each of those calls walks the whole list, so the loop around it multiplies the work.

## Level 2

A list lookup is linear, a hash set lookup is constant on average. With 80,000 tracks per playlist or tens of thousands of listeners per artist, list lookups inside a loop add up to billions of comparisons.

## Level 3

1. Replace lists used for "have I seen this" with hash sets.
2. Replace the list of artists searched with `indexOf` by a map from artist to its set of listeners.
3. Normalise each artist at most once per track if you can; normalising is the next most expensive step.
4. Keep the sorting at the end; it only sorts one entry per artist.
5. Run Phase 3 tests and compare each test's time with its limit.
