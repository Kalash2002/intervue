# Grader notes: Count Good Nodes in Binary Tree

Tests cover: the sample, a tree of negative values, a single node at the lowest value, ties all the way down, a falling path (only the root is good), a good node below a bad one, a node that beats its parent but not its grandparent, values at the constraint limits, a path of 1,000 nodes (the depth limit, so a recursive solution must still fit) in which every third node is a new maximum, and a tree of 100,000 nodes: a rising 983-node path on top of a complete block, depth exactly 1,000, where by construction the left children of the block are never good and the root and right children of the block always are (50,492 good nodes).

Traps from the walkthrough and hints, and the tests that grade them (each checked with a scratch version):
- Starting the running maximum at 0: fails `allNegative`, `singleNode`, `valuesAtTheLimits` and `largeTree`.
- Counting only strictly larger values as good: fails `tiesCount` (and nearly every other test).
- Comparing with the parent only: fails `allNegative`, `beatingTheParentIsNotEnough` and `largeTree`.

Depth is capped at 1,000 in the constraints so the recursive solution is safe; the reference is recursive.

No naive solution: the slower correct approach, scanning the whole path above every node (O(n·h)), was measured on a cold JVM on `largeTree`, where most nodes sit about 1,000 levels deep. Reference 51, 60 and 89 ms; the path scan 142 and 144 ms, about 2 to 3 times slower, far short of the 36 times the timing rule needs (reference at most limit/8, naive at least 1.5 x 3 x limit). So time is not graded separately for this question (ADR 0008). The 2-second limit on `largeTree` is generous.
