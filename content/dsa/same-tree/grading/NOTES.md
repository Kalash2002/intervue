# Grader notes: Same Tree

Tests cover: the sample, the same values with one node on the other side, one tree empty and the other not, both empty, one value that differs on the bottom level, one tree with an extra leaf, mirror images, values at the constraint limits, two right paths of 1,000 nodes (the depth limit, so a recursive solution must still fit) that are equal and then differ at the last node, and two complete trees of 100,000 nodes that are equal and then differ at the rightmost node, the one a left-first walk compares last. Every small test asks both ways round (`isSame(p, q)` and `isSame(q, p)`), so a check that only handles one side being empty fails.

Traps from the walkthrough and hints, and the tests that grade them:
- Comparing the values in preorder without marking empty children: a scratch version fails `sameValuesOnTheOtherSide` (both trees read 7, 2, 4, 9).
- Mishandling a missing child (treating one empty side as a match, or reading a field of `null`): `oneEmptyOneNot` and `oneTreeHasAnExtraLeaf`.

Depth is capped at 1,000 in the constraints so the recursive solution is safe; the reference is recursive. On this machine a test thread overflows near 10,000 frames for small recursive methods when they run cold, so the cap leaves a wide margin.

No naive solution: there is no meaningfully slower correct approach (writing both trees out in full and comparing the results is also linear), so time is not graded separately for this question (ADR 0008). The 2-second limit on `largeTrees` is generous; the reference takes about 40 ms, most of it building four trees.
