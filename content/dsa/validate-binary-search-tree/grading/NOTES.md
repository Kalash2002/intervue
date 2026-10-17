# Grader notes: Validate Binary Search Tree

Rule used throughout: strict ordering, so any repeated value makes the tree invalid. Values span the whole `int` range.

Tests cover: the sample, a small value hidden in a right subtree (statement Example 2), a large value hidden in a left subtree, extreme values that are legal (`[MIN,null,MAX]`, `[MAX]`, `[MIN]`, `[MAX,MIN]`, `[0,MIN,MAX]`), equal values on the left, on the right and two levels apart, equal extreme values (`[MIN,MIN]`, `[MAX,null,MAX]`, a repeated `MIN` below `MIN`), one node, a rising right path of 1,000 nodes (the depth limit) that is valid and then invalid once its bottom value repeats its top, and a tree of 100,000 nodes: a 983-node path on top of a balanced block, depth exactly 1,000, first valid, then with the leftmost leaf of the block's right subtree changed to one less than the block's root, which keeps every parent-child pair in order.

Traps from the walkthrough and hints, and the tests that grade them (each checked with a scratch version):
- Comparing each node with its children only: fails `rightSubtreeHidesASmallValue`, `leftSubtreeHidesALargeValue`, `equalValuesAreNot` and `largeTree`.
- Range bounds in `int` starting at `Integer.MIN_VALUE` and `Integer.MAX_VALUE` with strict checks: fails `extremeValuesAreLegal`.
- The same `int` bounds with non-strict checks: fails `equalValuesAreNot` and `equalExtremeValuesAreNot`.
- In-order walk with the previous value starting at `Integer.MIN_VALUE`: fails `extremeValuesAreLegal`.
- Allowing equal values (non-strict comparisons with `long` bounds): fails `equalValuesAreNot` and `equalExtremeValuesAreNot`.

Depth is capped at 1,000 in the constraints so the recursive solution is safe; the reference is recursive with `long` bounds.

No naive solution: the slower correct approach, checking every node against the maximum of its left subtree and the minimum of its right subtree (O(n·h)), was measured on a cold JVM on `largeTree`. Reference 59, 61 and 65 ms; the subtree check 413 and 435 ms, about 7 times slower, short of the 36 times the timing rule needs (reference at most limit/8, naive at least 1.5 x 3 x limit). So time is not graded separately for this question (ADR 0008). The 2-second limit on `largeTree` is generous.
