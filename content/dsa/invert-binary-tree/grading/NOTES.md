# Grader notes: Invert Binary Tree

Tests cover: the sample, a tree that leans left all the way down, the empty tree (`null`), one node, a full tree of three levels, repeated values (the nodes, not just the values, must move), values at the constraint limits, a path of 1,000 nodes (the depth limit, so a recursive solution must still fit), and a complete tree of 100,000 nodes. Every test except the empty tree checks node identity: the returned root is the given root, and every node's new left child is its old right child and the other way round, so a solution that builds new nodes fails; the small ones also compare the level order of the result.

Traps from the walkthrough and hints, and the tests that grade them:
- Overwriting `root.left` before saving it, so both sides end up as the mirrored right subtree: a scratch version fails `sample` and every other test with two children somewhere.

Depth is capped at 1,000 in the constraints so the recursive solution is safe; the reference is recursive. On this machine a test thread overflows somewhere near 10,000 frames for small recursive methods when they run cold (interpreted), so 1,000 leaves a wide margin for candidate code with bigger frames.

No naive solution: there is no meaningfully slower correct approach (collecting the nodes first and then swapping is also linear), so time is not graded separately for this question (ADR 0008). The 2-second limit on `largeCompleteTree` is generous; the reference finishes in about 60 ms, most of it building and checking the tree.
