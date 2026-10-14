## Level 1

There are three workloads and three different causes. Time each one alone. The nightly feed is slow before `Suggester` is ever called.

## Level 2

Loading is slow because `MerchantDirectory.add` inserts into the middle of lists. Specific searches are slow because every call scans every merchant. Letter-by-letter typing repeats the same short prefixes, each matching tens of thousands of merchants. Fix each with its own structure: cheaper inserts, a sorted index of every word start, and remembered answers per prefix that are dropped when the directory changes.

## Level 3

1. Change how `add` stores merchants so one insert does not shift half the directory.
2. Build an index of every word-start suffix of every normalized name, sorted, so the matches for a prefix form one range found by binary search.
3. Remember the answer for each prefix and limit, and reuse it for repeated keystrokes.
4. Give the directory a version that changes on every add or replace, and rebuild the index and forget remembered answers when it changes. A size check misses a replaced merchant.
5. Run Phase 3 tests, then all tests; the phase 2 freshness test must still pass.
