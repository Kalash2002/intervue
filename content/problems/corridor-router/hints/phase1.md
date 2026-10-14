## Level 1

`fewestHops` is a breadth-first search. Look at the moment a node is marked as seen, and at when `arrivedBy` is written for it.

## Level 2

A node is marked as seen when it is taken off the queue instead of when it is put on. Until then it can be queued again from other nodes, and each time its `arrivedBy` is overwritten by a later, longer way of reaching it. The search still ends, but the route rebuilt from `arrivedBy` can have extra hops, and whether a test fails depends on the order corridors were added.

## Level 3

1. Find where the search adds a node to the queue and where it records how the node was reached.
2. Mark the node as seen at the moment it is first queued, and record how it was reached only then.
3. Run Phase 1 tests; the direct INR to USD corridor should now win.
4. Be ready to state the cost: every node and corridor is handled once, so time grows with nodes plus corridors.
