# Grader notes: Reverse Nodes in K-Group

Tests cover: the sample (a short last group of one), a length that is a multiple of `k`, `k = 1`, `k` equal to the length, short last groups of two and of one after a group of four, a single node, repeated values (the nodes, not just the values, must come back in the new order), values at the constraint limits, and a list of 100,000 nodes with `k` = 2, 7, 99,998 and 100,000 (7 and 99,998 leave short groups of 5 and 2). Every list check walks the result with a node limit, so a loop fails instead of hanging, and checks node identity position by position, so building new nodes or swapping values fails.

Traps from the walkthrough and the tests that grade them:
- Reversing the short last group too: a scratch version fails `shortLastGroupStays` and `longList`.
- Swapping values instead of relinking nodes: a scratch version fails 7 of 9 tests on node identity.
- Recursion with a small `k` on a long list: a scratch version of the walkthrough's Better passes every small test and fails `longList` with `StackOverflowError` (50,000 groups at `k = 2`).

The walkthrough's Brute force (an array of nodes) passes every test (160 ms on `longList`).

No naive solution: there is no meaningfully slower correct approach (the array version is also linear), so time is not graded separately for this question (ADR 0008). The 2-second limit on `longList` is generous; the reference finishes in 90 to 137 ms for all four values of `k`.
