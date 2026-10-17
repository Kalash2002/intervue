# Grader notes: Maximum Depth of Binary Tree

Tests cover: the sample, a path that only goes right, the empty tree (0), one node (1), a tree whose deepest leaf is on the left while the right side is shallower, values at the constraint limits, a complete tree of 100,000 nodes (17 levels), and a path of 1,000 nodes (the depth limit, so a recursive solution must still fit).

Traps from the walkthrough and hints, and the tests that grade them:
- Counting edges instead of nodes: `singleNodeCountsAsOne` (and every other test) fails with an answer one too small.

Depth is capped at 1,000 in the constraints so the recursive solution is safe; the reference is recursive. On this machine a test thread overflows somewhere near 10,000 frames for small recursive methods when they run cold (interpreted), so 1,000 leaves a wide margin for candidate code with bigger frames.

No naive solution: there is no meaningfully slower correct approach (every correct answer visits each node once), so time is not graded separately for this question (ADR 0008). The 2-second limit on `largeCompleteTree` is generous; the reference takes about 25 ms, most of it building the tree.
