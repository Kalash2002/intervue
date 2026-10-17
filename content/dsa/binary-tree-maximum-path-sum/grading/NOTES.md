# Grader notes: Binary Tree Maximum Path Sum

Tests cover: the sample, a best path that turns below the root (statement Example 2), trees where every value is negative (the answer is the largest single value, down to one node of -10,000), single nodes of 10,000 and 0, a branch that must be dropped because it sums below zero, a fork that must not be extended upwards, paths of 1,000 nodes leaning left and right (the depth limit, so a recursive solution must still fit) with values alternating 3 and -1 and with every value 10,000, and two trees of 100,000 nodes, each a 983-node path of left children on top of a balanced block of 99,017 nodes, depth exactly 1,000. In the first the block holds -1s and the path -50s then 10s, so the best path is the 883 tens (8,830); in the second the block holds 1s and the path -10,000s, so the best path is the longest one in the block, 16 nodes down each side of its root plus the root (33). Expected values come from how the trees are built. Sums stay below 2 * 10^7, so `int` is safe.

Traps from the walkthrough and hints, and the tests that grade them (each checked with a scratch version):
- Starting the best sum at 0 instead of the smallest `int`: fails `allNegative`.
- Adding a child's downward sum even when it is negative: fails `losingBranchIsLeftOut`.
- Handing the parent the path through both children (the fork) instead of one side: fails `forkCannotBeExtended` and `largeTree`.

Depth is capped at 1,000 in the constraints so a recursive solution is safe; the reference is recursive.

The naive solution starts a walk at every node and follows every path from it, up and down, O(n^2). Measured with a cold JVM on `largeTree` (limit 2,000 ms): reference 72 ms (at most 250 = limit/8; under limit/12 = 166); naive still running after 10,000 ms at 5 times the limit (needs 9,000 = 1.5 x 3 x limit), as expected for about 10^10 steps. The walkthrough's Better, a fresh downward walk from each node (O(n·h)), took 593 ms there: it passes, since the depth cap holds n·h to about 10^8. Time is graded against the quadratic walk, not against the O(n·h) one.
