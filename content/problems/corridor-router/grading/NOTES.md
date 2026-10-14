# Grader notes: Corridor Router

**Phase 1 bug.** `fewestHops` marks a node as visited when it is taken off the queue, not when it is put on. A node can therefore be queued several times, and each time `arrivedBy` is overwritten with a later, longer way of reaching it. The search still ends, but the rebuilt route can have extra hops. Result depends on the order corridors were added, which is why some tests pass by luck. Fix: mark on enqueue (`if (active && seen.add(to))`), or only record `arrivedBy` the first time.
Cost: O(V + E) time, O(V) space.

**Phase 2.** Hop-limited cheapest path. Plain Dijkstra by fee is wrong because the cheapest way to reach an intermediate node may use too many hops. Correct options: Bellman-Ford limited to `maxHops` rounds (each round must read the previous round's costs, not the ones being written), or Dijkstra over (node, hops used) states. Tie on fee must prefer fewer hops. Enumerating all paths depth-first is correct but exponential.
Cost of the reference: O(maxHops x E) time per table, O(maxHops x V) space.

**Phase 3.**
- Workload 2 (40 hops) kills path enumeration.
- Workload 3 needs the table for (origin, maxHops) to be reused across lookups, since one table answers every destination. The cache must be dropped when the network changes; the final assertion adds a corridor and checks the answer. Strong candidates name this risk before the test catches it. Indexing nodes into arrays helps but is not required.
- A candidate who cannot finish but explains "one table per origin answers all destinations, invalidate on change" has the insight.

**Common assistant mistakes to watch for.** Plain Dijkstra ignoring the hop limit; relaxing in place so a round uses more hops than allowed; a cache with no invalidation; a cache keyed by origin only, ignoring `maxHops`.
