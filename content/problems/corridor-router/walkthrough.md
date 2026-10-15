# How a strong round goes: Corridor Router

**Read this first.** This is one good way through the problem, not the only one. The complexity figures describe the reference solution. The practice links go to other sites and to different questions that use the same technique; they are not this question. The prompts are written for interview mode, where the assistant does what you specify and nothing more.

## Minutes 0 to 7: read before you run

Say aloud: "I'll take a few minutes to read the data model and the existing search, then run the demo."

1. `Corridor.java` and `Route.java`: a corridor is one-way, has a fee in minor units and an `active` flag. A route is a list of stops and a total fee; `hops()` is stops minus one.
2. `Network.java`: a map from node to the corridors leaving it, in the order they were added. `add` can be called at any time, so the network is not fixed. `corridorCount()` goes up on every add.
3. `RouteFinder.java`: `fewestHops` is a breadth-first search with a queue, a `visited` set and an `arrivedBy` map used to rebuild the route. `cheapest` throws.
4. `data/corridors.txt`: 15 lines. INR has three outgoing corridors, in this order: SGD, AED, USD.
5. Run `Main`. It prints `INR > AED > USD` for fewest hops although `INR>USD` exists. You have reproduced the ticket before opening a test.

## Prompt: understand the codebase fast

```text
Read-only question. For this project give me:
1. One line per class: what it owns and which other classes it calls.
2. In RouteFinder.fewestHops: every collection it uses, what each one stores, and the exact line where each one is written to.
3. Whether Network can change after a RouteFinder is created, and which method changes it.
4. How the order of lines in data/corridors.txt affects the order of Network.corridorsFrom.
Keep it under 200 words. Mark anything you are inferring.
```

Check it by opening the lines it cites for item 2. Items 2 and 4 set up phase 1; item 3 sets up phase 3.

## Phase 1: one hop too many (minutes 7 to 15)

### Reproduce

Run phase 1 tests. `directCorridorBeatsADetour` and the two sample-network tests fail; six pass. The failing test has three corridors: `A>C`, `A>B`, `C>B`. Expected `[A, B]`, actual `[A, C, B]`.

### Trace it by hand, aloud

"Start: queue is A. Take A, mark it visited, queue C and B, and record that B was reached from A. Take C. B is not marked visited yet, because marking happens when a node is taken off the queue. So B is queued a second time, and `arrivedBy` for B is overwritten: B now says it was reached from C. Take B, it is the target, rebuild the route from `arrivedBy`: A, C, B."

A three-node trace like this is the fastest tool you have. If you want a second opinion, ask something specific:

```text
In RouteFinder.fewestHops, a node is added to visited when it is polled from the queue, and arrivedBy.put runs every time a corridor leads to a node that is not yet visited. Can arrivedBy for one node be written more than once? Walk through the corridors A>C, A>B, C>B in that insertion order.
```

### The cause

Nodes are marked visited when they leave the queue, not when they enter it. A node waiting in the queue can be reached again by a longer way, and the later write to `arrivedBy` replaces the shorter one. The search still ends, so nothing crashes; the route is just longer than necessary. It depends on insertion order, which is why most tests pass.

### The fix

Mark on enqueue, so the first corridor to reach a node wins. In a breadth-first search the first arrival is always by a shortest route.

```java
queue.add(from);
visited.add(from);
while (!queue.isEmpty()) {
    String current = queue.poll();
    if (current.equals(to)) {
        return Optional.of(buildRoute(from, to, arrivedBy));
    }
    for (Corridor corridor : network.corridorsFrom(current)) {
        if (corridor.active() && visited.add(corridor.to())) {
            arrivedBy.put(corridor.to(), corridor);
            queue.add(corridor.to());
        }
    }
}
```

`arrivedBy.putIfAbsent` in the original loop is a smaller fix that also works.

### Say this

"Breadth-first search visits nodes in order of hop count, so the first time a node is discovered is by a shortest route. The bug let a later discovery overwrite the first. Time O(nodes + corridors), space O(nodes)."

## Phase 2: cheapest route with a hop limit (minutes 15 to 38)

### Analysis before any prompt

- Two things to minimise would be hard; here only the fee is minimised and hops are a limit. Ties on fee prefer fewer hops.
- **A plain cheapest-path search is wrong.** The cheapest way to reach a middle node may use so many hops that the destination can no longer be reached within the limit, while a pricier way to that node would have worked. The state is (node, hops used), not node alone.
- Fees can be zero and there are loops, so the search must not depend on fees being positive to stop.

### Decide the approach yourself

"I'll do rounds. `cost[h][v]` is the lowest fee to reach v using at most h corridors. Round h reads round h minus 1 and tries every active corridor once. After `maxHops` rounds, `cost[maxHops][to]` is the answer. For the tie-break I take the smallest h whose cost already equals that answer, then walk back through the corridor I recorded for each improvement."

This is the Bellman-Ford algorithm stopped after `maxHops` rounds.

### Prompt

```text
In RouteFinder, implement cheapest(from, to, maxHops) as Bellman-Ford limited to maxHops rounds.
- Throw IllegalArgumentException if maxHops < 0. Return Optional.empty() if either node is unknown.
- Keep one cost array per round: round h must read only round h-1 (copy the previous array, then relax every active corridor from the previous array), so a round never uses more than h corridors.
- For each round keep which corridor improved each node, or nothing if the cost was carried over.
- Answer: the cost at the last round. Among rounds with that same cost for the target pick the earliest, then rebuild the stops backwards from that round.
- from equal to to gives Route([from], 0).
Plain Java 21. Only this method and private helpers.
```

### Check the reply for

- **Relaxing in place.** If the round writes into the same array it reads, one round can chain several corridors and the hop limit is broken. `hopLimitForcesThePricierRoute` catches it.
- **Plain Dijkstra.** If the reply ignores your instruction and uses a priority queue keyed by fee with a visited set of nodes, it fails the same test.
- Inactive corridors skipped; parallel corridors handled by taking the cheaper.
- The tie-break: `equalFeePrefersFewerHops`.

Run phase 2 tests. Read the route validation helper in `Phase2Tests` so you know what "valid" means.

## Phase 3: large networks (minutes 38 to 55)

### Read the three tests first

- `manyUnrelatedLookups`: 3,000 nodes, 45,000 corridors, 60 lookups, 7 hops. One table costs about 7 x 45,000 steps. Fine with the phase 2 approach.
- `longRoutesOnASparseNetwork`: up to 40 hops. Any approach that enumerates paths dies here. Rounds over corridors do not care.
- `settlementBatchFromOneOrigin`: 6,000 lookups from the same origin with the same hop limit. Then a corridor is added and the next answer must change.

Say the insight aloud: "One table for an origin and a hop limit answers every destination. The batch asks 6,000 destinations of one origin, so I should build the table once and reuse it. The last lines of the test add a corridor, so the reused table must be thrown away when the network changes."

### Prompt

```text
In RouteFinder, keep the cost and corridor tables from the last call to cheapest in fields, together with the origin, maxHops and network.corridorCount() they were built for. Reuse them when all three match; rebuild otherwise. Do not change the public methods. Show only the changed parts.
```

`corridorCount()` works as a change signal because corridors are only ever added. Say that this would not be enough if corridors could be switched off in place; then `Network` needs a version counter.

### Check the reply for

- A cache keyed by origin only. A different `maxHops` must rebuild.
- No invalidation at all. The final assertion in the batch test catches it; name the risk before the test does.
- Per-lookup route rebuilding should walk back through the stored table, not recompute.

Optional and only if time remains: index nodes as integers and keep corridors in arrays. It speeds up the rounds but none of the tests require it.

## The technique: breadth-first search, and Bellman-Ford with a round limit

**Breadth-first search.** Explores nodes in order of distance in hops. Use it when every edge counts the same. The rule that makes it correct: a node is claimed the first time it is discovered, which means when it is put on the queue.

**Why not Dijkstra here.** Dijkstra settles each node once, at its cheapest fee. With a hop limit the cheapest arrival at a node is not always the useful one, so "settle once" is wrong. Either add hops to the state, or use rounds.

**Bellman-Ford with k rounds.** After round h, every node holds its cheapest fee using at most h edges. Each round reads the previous round and tries every edge. It handles zero-fee loops without special care because the number of rounds is fixed.

**How to recognise it.** "Cheapest path with at most k stops", "best result using at most k steps", or any shortest-path question with a second limit on length.

| Approach | Time per lookup | Space | Verdict |
| --- | --- | --- | --- |
| Enumerate every path up to k hops | Exponential in k | O(k) | Correct on tiny inputs, times out |
| Dijkstra by fee, nodes settled once | O(E log V) | O(V) | **Wrong** with a hop limit |
| Dijkstra over (node, hops) states | O(k x E x log(k x V)) | O(k x V) | Correct |
| Bellman-Ford, k rounds | O(k x E) | O(k x V) with route rebuilding | Correct, simplest |
| Same, table reused per origin | O(k x E) once, then O(k) per destination | O(k x V) | Needed for the batch |

V is nodes, E is active corridors, k is `maxHops`. `fewestHops` is O(V + E) time and O(V) space.

**Practise the technique**

These open inside the app under DSA.

- [Cheapest Flights Within K Stops](dsa://cheapest-flights-within-k-stops): the same shape as phase 2: the round-by-round version, and why a copy of the previous round is needed.
- [Network Delay Time](dsa://network-delay-time): the cheapest route without a hop limit, to see what the limit changes.

## Time budget

| Minutes | What |
| --- | --- |
| 0 to 7 | Read the records, `Network`, `fewestHops`, the data file; run `Main` |
| 7 to 15 | Phase 1: three-node trace by hand, mark on enqueue, re-run |
| 15 to 20 | Phase 2 analysis aloud: why the state is (node, hops), choose rounds |
| 20 to 38 | Phase 2: implement, check for in-place relaxing, run |
| 38 to 43 | Phase 3: read the three tests, state the one-table insight, run |
| 43 to 55 | Phase 3: reuse the table, invalidate on change, re-run everything |
| 55 to 60 | Read your diff; say what a version counter would add |

## Follow-up answers

**Cost of the cheapest-route search.** O(k x E) time to build a table and O(k x V) space, for V nodes, E active corridors and k hops. With the table reused, later lookups from the same origin cost O(k) to rebuild the route.

**Why plain shortest-path by fee is wrong with a hop limit.** It keeps only the cheapest way to each node. That way may use more hops than a pricier one, and the pricier one may be the only one that still reaches the destination within the limit.

**When the cache is dropped, and threads.** It is dropped when the origin, the hop limit or the corridor count differs from what it was built for. The fields are plain and unguarded, so two threads could interleave a rebuild and a read; either confine a `RouteFinder` to one thread, synchronise `cheapest`, or publish an immutable table object through a single volatile field.

**Corridors switched off in production.** The router needs a change signal from the network, such as a version number that every mutation increments, and should compare it before reusing a table. A payment already in flight was routed on old data; the usual answer is to re-validate the next corridor at each hop and re-route from the current node if it is gone.

**What the assistant got wrong.** Answer from your own session. Typical: plain Dijkstra, relaxing in place, a cache keyed by origin only, or no invalidation.
