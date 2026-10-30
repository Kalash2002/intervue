# Grader notes: Diameter of Binary Tree

Tests cover: the sample, a tree whose longest path turns below the root, one node (0 edges), two nodes, values at the constraint limits, a path of six nodes, a path of 1,000 nodes (the depth limit, so a recursive solution must still fit), and a "handle over a block": a path of 983 nodes on top of a complete tree of 98,017 nodes (99,000 nodes, depth exactly 1,000), whose longest path runs from the top of the handle to the deepest leaf, 999 edges.

Traps from the walkthrough and hints, and the tests that grade them:
- Only measuring the path through the root (left height plus right height at the root): a scratch version fails `longestPathMissesTheRoot` (3 instead of 4).
- Counting nodes instead of edges: a scratch version that adds one at the turning node fails `singleNode` (1 instead of 0) and every other test.

Depth is capped at 1,000 in the constraints so the recursive solution is safe; the reference is recursive. On this machine a test thread overflows near 10,000 frames for small recursive methods when they run cold, and the brute force (heights recomputed under a recursive diameter call) overflowed a cold path of 5,000 nodes, so the cap leaves a wide margin.

No naive solution. The brute force (recompute both heights at every node, O(n·h)) is meaningfully slower only on tall trees, and the depth cap that keeps recursion safe also caps the gap. Measured on a cold JVM on `longHandleOverABigBlock` (depth 1,000): reference 17, 21 and 27 ms, brute force 357, 378 and 411 ms, a factor of about 15, short of the factor of 36 the timing rule needs (reference at most limit/8, naive at least 1.5 x 3 x limit). At depth 2,000 it was 15 to 23 ms against 521 ms, still short. Time is therefore not graded separately for this question (ADR 0008); the test keeps a generous 2-second limit.
