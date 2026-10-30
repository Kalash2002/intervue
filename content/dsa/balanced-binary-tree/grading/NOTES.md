# Grader notes: Balanced Binary Tree

Tests cover: the sample, a tree whose root is balanced but whose left child is not, the empty tree (true), one node, a path of two nodes (balanced), a path of three nodes (not), a balanced tree whose leaves lie two levels apart (depths 2 and 4), a path of 1,000 nodes (the depth limit, so a recursive solution must still fit), a complete tree of 100,000 nodes (balanced), and a complete tree of 99,998 nodes whose leftmost leaf on the deepest level was given a two-node tail (not balanced, the flaw sits 17 levels down).

Traps from the walkthrough and hints, and the tests that grade them:
- Comparing heights only at the root: a scratch version fails `rootLooksFineButAChildIsNot`.
- Comparing the shallowest and the deepest leaf instead of heights at every node: a scratch version fails `sample` and `leavesMayBeTwoLevelsApart`.

Depth is capped at 1,000 in the constraints so the recursive solution is safe; the reference is recursive. On this machine a test thread overflows near 10,000 frames for small recursive methods when they run cold, so the cap leaves a wide margin.

No naive solution. The brute force (compare the two heights at a node, then check both children the same way) stops at the first unbalanced node, so it only keeps descending through nodes that are balanced themselves, and those cannot form a long thin chain. Its cost is O(n·h) as an upper bound, but on a balanced tree that is O(n log n), and a tree built to make it work hard, a long left spine of balanced nodes each with a thin path as its right child, costs it about n^1.5/6 steps, around 1.5 x 10^7 for 100,000 nodes. Measured on a cold JVM with that tree (a spine of 447 nodes, 99,237 nodes in all, depth 447): reference 6 ms, brute force 92 ms, a factor of about 15, short of the factor of 36 the timing rule needs (reference at most limit/8, naive at least 1.5 x 3 x limit). So time is not graded separately for this question (ADR 0008). The 2-second limits are generous; the reference takes under 60 ms on the large tests.
