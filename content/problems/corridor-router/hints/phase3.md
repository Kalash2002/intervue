## Level 1

Run each workload on its own and note which one fails. The long-route workload and the settlement batch stress different things.

## Level 2

Exploring every possible path explodes when routes can be 40 corridors long, so the phase 2 method must grow with hops times corridors. The settlement batch asks thousands of questions from the same origin with the same limit, and one table of fees from an origin already answers every destination.

## Level 3

1. Make sure the cheapest-route search does a fixed number of rounds over the corridors, with no path enumeration.
2. Remember the computed table per origin and hop limit, and answer later lookups from it.
3. Key the remembered tables by both origin and hop limit, not origin alone.
4. Throw the remembered tables away whenever the network changes; the last assertion adds a corridor and expects the new answer.
5. Run Phase 3 tests, then all tests.
