# Grader notes: Kth Smallest Element in a BST

Tests cover: the sample, the largest value (`k` = n), the smallest (`k` = 1), every `k` on the sample tree, a single node at the lowest value, negative and extreme values, a falling left path and a rising right path of 1,000 nodes (the depth limit, so a recursive solution must still fit), and 2,000 calls with random `k` from 1 to 1,000 on a tree of 100,000 nodes, a 983-node path on top of a balanced block, depth exactly 1,000, plus one call with `k` = 100,000. Expected values come from how the trees are built.

Traps from the walkthrough and hints, and the tests that grade them:
- Counting from 0 instead of 1 (an off-by-one in `k`): fails `sample` and every other test.
- Walking the whole tree on every call (collecting all values, sorted or in order): fails `manySmallKInALargeTree` on time.

Depth is capped at 1,000 in the constraints so a recursive solution is safe; the reference is an iterative in-order walk that stops at `k`, O(h + k) per call.

The naive solution collects every value with a stack and sorts them, O(n log n) per call. Measured with a cold JVM on `manySmallKInALargeTree` (limit 700 ms): reference 49, 53 and 62 ms over three runs (at most 87 = limit/8); naive still running after 12,000 ms with 20x the limit (needs 3,150 = 1.5 x 3 x limit). The walkthrough's Better, a full in-order walk into a list with no sort (O(n) per call), took 1,733 ms there, so it also fails that test: the question asks for the early stop, and the statement says so: each call should cost time that depends on the depth and on `k`, with the test's scale (about 2,000 calls, `k` up to 1,000, 100,000 nodes) given.
