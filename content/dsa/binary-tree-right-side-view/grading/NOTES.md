# Grader notes: Binary Tree Right Side View

Tests cover: the sample, a tree whose top levels have only left children, the empty tree (an empty list, not `null`), one node, a full tree, a left side that reaches two levels deeper than the right, repeated values at the constraint limits, a left path of 1,000 nodes (the depth limit, so a recursive solution must still fit), and a tree of 100,000 nodes: a complete block of 99,017 nodes (17 levels) with a 983-node path hanging under the last node of its bottom level, which is not on the block's right edge, depth exactly 1,000. The large test's expected values come from how it is built.

Traps from the walkthrough and hints, and the tests that grade them (each checked with a scratch version):
- Following right children only: fails `sample`, `onlyLeftChildrenNearTheTop`, `leftSideReachesDeeper`, `repeatedValuesAndLimits`, `deepestAllowedPath` and `largeTreeWithALongTail`.
- Following the right child, or the left one when there is no right child: fails `sample`, `leftSideReachesDeeper`, `repeatedValuesAndLimits` and `largeTreeWithALongTail`.
- Walking left side first and keeping the first node per depth (the left view): fails `sample`, `fullTree`, `leftSideReachesDeeper` and `largeTreeWithALongTail`.

Depth is capped at 1,000 in the constraints so the recursive solution is safe; the reference is recursive.

No naive solution: every correct approach in the walkthrough is linear, and the slower one-walk-per-level idea is the same O(n·h) brute force as in Binary Tree Level Order Traversal, measured there at only 5 to 8 times the reference within the depth cap, short of the 36 times the timing rule needs. So time is not graded separately for this question (ADR 0008). The 2-second limit on `largeTreeWithALongTail` is generous; the reference finishes in about 50 ms.
