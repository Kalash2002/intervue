# Grader notes: Construct Binary Tree from Preorder and Inorder Traversal

Tests cover: the sample, a zigzag of three nodes (statement Example 2), a path leaning left (pre-order is in-order reversed), one node at each value limit, a path leaning right with extreme values (pre-order equals in-order), four uneven trees whose traversals the test computes from the tree, three calls in a row, paths of 1,000 nodes leaning left, leaning right and zigzagging (the depth limit, so a recursive solution must still fit), and a tree of 100,000 nodes: a 983-node path of left children on top of a balanced block, depth exactly 1,000. Every test compares the level order of the returned tree with the expected one; expected values come from how the trees are built.

Traps from the walkthrough and hints, and the tests that grade them (each checked with a scratch version):
- Off by one in where the right subtree starts in pre-order (`preStart + leftSize` instead of `preStart + leftSize + 1`): fails `sample`, `zigzag`, `unevenSubtrees` and the larger tests.
- A pre-order cursor kept in a static field and never reset: the second call reads past the end, so it fails `twoCallsInARow` (and every test after the first in the same run).

Depth is capped at 1,000 in the constraints so a recursive solution is safe; the reference is recursive.

No naive solution: time is not graded separately for this question (ADR 0008). The natural slower approach, scanning in-order for each root, costs the sum of the left-subtree sizes, at most n times the depth, so the depth cap holds it to about 10^8 steps. Measured with a cold JVM on `largeTree`, whose 983 left children make that scan as long as the cap allows: reference 60 ms, scan 199 ms, about 3 times slower, far short of the 36 times the timing rule needs (reference at most limit/8, naive at least 1.5 x 3 x limit). The 1,000 ms limit on `largeTree` leaves the reference more than 12 times headroom; the hash-map version passes it too.
