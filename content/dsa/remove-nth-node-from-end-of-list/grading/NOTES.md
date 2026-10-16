# Grader notes: Remove Nth Node From End of List

Tests cover: the sample, removing the head, removing the last node, removing the only node (the answer is `null`), both removals from a two-node list, repeated values (the node at the right position must go, checked by identity), values at the constraint limits, and a list of 100,000 nodes with `n` = 1, `n` = 100,000 (the head) and `n` = 37,000. Every check walks the result with a node limit (one fewer than the input), so a solution that removes nothing or leaves a loop fails instead of hanging, and checks that every remaining node is an original node in its original order.

Traps from the walkthrough and the tests that grade them:
- Removing the head without a dummy node: a scratch version without one fails `removeTheHead`, `singleNodeLeavesAnEmptyList`, `twoNodes` and `longList` (`n` = 100,000).
- An off-by-one gap between the two pointers: a scratch version that sends the lead pointer `n - 1` nodes ahead removes the wrong node and fails every test.

No naive solution: the slower approaches in the walkthrough (copying the nodes into an array, or counting and then walking) are still linear, not meaningfully slower, so time is not graded separately for this question (ADR 0008). The 2-second limit on `longList` is generous; the reference takes about 0.1 s for the three long lists, most of it building and checking them.
