# Grader notes: Merge Two Sorted Lists

Tests cover: the sample (where `second` has two values left after `first` runs out), equal values within and across the lists (including two single nodes with the same value), either list empty, both empty (`null`), one list entirely before the other in both argument orders, negative values and the constraint limits, and two interleaved lists of 100,000 nodes each. Every check walks the result with a node limit (a looping result fails instead of hanging) and requires each given node exactly once, so building new nodes fails.

Traps from the walkthrough and the tests that grade them:
- Dropping the leftover tail when one list runs out: `sample`, `oneListEntirelyBeforeTheOther`, `firstListEmpty` and `secondListEmpty`.
- Recursion on long lists: a scratch recursive merge passes every small test and fails `longInterleavedLists` with `StackOverflowError` (200,000 nested calls).

No naive solution: there is no meaningfully slower correct approach (collecting the nodes and sorting them is O((m + n) log(m + n)), too close to linear to fail a time limit reliably), so time is not graded separately for this question (ADR 0008). The 2-second limit on the long test is generous.
