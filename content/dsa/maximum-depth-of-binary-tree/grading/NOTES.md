# Grader notes: Maximum Depth of Binary Tree

Tests cover: the sample, a path that only goes right, the empty tree (0), one node (1), a tree whose deepest leaf is on the left while the right side is wider, values at the constraint limits, a complete tree of 100,000 nodes (17 levels), and a path of 100,000 nodes.

Traps from the walkthrough and hints, and the tests that grade them:
- Counting edges instead of nodes: `singleNodeCountsAsOne` (and every other test) fails with an answer one too small.
- Recursion on a lopsided tree: the constraints allow a single path of 100,000 nodes; a scratch one-line recursive version passes every other test and fails `longPathOfAllTheNodes` with `StackOverflowError`.

The reference counts levels with a queue (breadth-first), so its extra space is the widest level and nothing lives on the call stack.

No naive solution: there is no meaningfully slower correct approach (every correct answer visits each node once), so time is not graded separately for this question (ADR 0008). The 2-second limits are generous; the reference takes about 25 ms on each large test, most of it building the tree.
