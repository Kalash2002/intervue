# Grader notes: Lowest Common Ancestor of a BST

Tests cover: the sample, one node above the other (both ways), a split at the root, the root as one of the pair, a two-node tree, values far apart (up to ±10^9), a path of 1,000 nodes (the depth limit), and about 20,000 random pairs in a balanced tree of 100,000 nodes (17 levels, values spread over ±10^9). Every small test asks with `p` and `q` both ways round and checks node identity: the answer must be the very node of the tree, not a node with an equal value. The large test checks each answer without knowing it in advance: the returned node must lie between `p` and `q` in value and reach both by a search from it, which only the lowest common ancestor does.

Traps from the walkthrough and hints, and the tests that grade them (each checked with a scratch version):
- Multiplying the two differences in `int`, `(node.val - p.val) * (node.val - q.val) > 0`: fails `valuesFarApart` (at 0 with -65,536 and -131,072 the product 2^33 wraps to 0 and the walk stops at the root) and `manyPairsInALargeTree`.
- Assuming `p` is the smaller one, or forgetting that one may be above the other: every small test asks both ways round; `oneIsAboveTheOther` and `rootIsOneOfThem` cover the second.

Depth is capped at 1,000 in the constraints so the recursive answers are safe; the reference is the loop.

The naive solution is the general binary-tree answer that ignores the ordering: O(n) per pair, recursive (depth at most 1,000). The reference walks down by value, O(h) per pair. The large test uses a balanced tree so that the reference's walks are short and its time is mostly building the tree; the depth limit is exercised by `deepestAllowedPath`. Measured with a cold JVM on `manyPairsInALargeTree` (limit 700 ms), on a machine that was busy with other work: reference 53, 67 and 77 ms over three runs (best 53, at most 87 = limit/8); naive 6,071 ms with 20x the limit (needs 3,150 = 1.5 x 3 x limit). An earlier version of the test with a 983-node path on top of the block made the reference walk about 1,000 nodes per pair; it measured 71 ms on a quiet machine but 190 ms under load, too close to the limit, so the path moved to its own test.
