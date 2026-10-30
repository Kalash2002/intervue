# Grader notes: Binary Tree Level Order Traversal

Tests cover: the sample, a zigzag path, the empty tree (an empty list, not `null`), one node, gaps inside a level (a level whose values come from non-adjacent parents), repeated values, values at the constraint limits, a path of 1,000 nodes (the depth limit, so a recursive solution must still fit), and a tree of 100,000 nodes: a complete block of 99,017 nodes (17 levels) with a 983-node path hanging under the last node of its bottom level, depth exactly 1,000. The large test's expected levels come from how it is built, and a mismatch names the first level that differs.

Traps from the walkthrough and hints, and the tests that grade them:
- Reading the queue without level boundaries (one flat list, or a new level per node): fails `sample` and every test with two nodes on one level.
- Visiting right before left: fails `sample` and `gapsInsideALevel`.

Depth is capped at 1,000 in the constraints so a recursive solution is safe; the reference uses a queue.

No naive solution: the one slower correct approach, one walk from the root per level (O(n·h)), was measured on a cold JVM on `largeTreeWithALongTail` and is not slow enough within the depth cap. Reference 72, 75 and 100 ms; per-level walks 373 and 629 ms, about 5 to 8 times slower, short of the 36 times the timing rule needs (reference at most limit/8, naive at least 1.5 x 3 x limit). So time is not graded separately for this question (ADR 0008). The 2-second limit on `largeTreeWithALongTail` is generous.
