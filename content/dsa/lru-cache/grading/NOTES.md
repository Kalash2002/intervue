# Grader notes: LRU Cache

Tests drive the class through call sequences: the sample, an update that refreshes a key (so the other key is evicted next), an update while full that must evict nothing, capacity 1, a `get` hit that refreshes a key, misses on an empty cache with key and value 0 and the largest key and value, a miss that must not change the order, two caches used side by side (no shared static state), and a long run (capacity 200,000 filled first, then 400,000 random calls, half `get` and half `put`, on keys from 0 to 249,999, fixed seed) checked call by call against the JDK's access-ordered `LinkedHashMap`.

Traps from the walkthrough and hints, and the tests that grade them:
- A `get` hit that does not refresh the key: a scratch version fails `sample`, `getRefreshesTheKey` and `largeRun`.
- An update that does not refresh the key: a scratch version fails `updateRefreshesTheKey` and `largeRun`.
- Evicting before checking whether the key is already present: a scratch version fails `updateWhenFullEvictsNothing` and `largeRun`.

The walkthrough's Better (`LinkedHashMap` in access order) is meant to pass: a scratch version takes 271 ms on `largeRun`.

The naive solution keeps the keys in an array list from least to most recently used, so every hit and update scans and shifts the list, O(capacity) per call. Measured with a cold JVM (limit 4,000 ms on `largeRun`): reference 280, 288 and 290 ms over three runs (at most 500 = limit/8); naive 49.1 s when given 100x the limit (needs 18 s = 1.5 x 3 x limit; 4x past the 3x line of 12 s). The capacity goes up to 2 * 10^5 and a cache sees up to 6 * 10^5 calls, more than the usual sizes, so that this gap exists. The reference is a hash map from key to the node of a doubly linked list with sentinels.
