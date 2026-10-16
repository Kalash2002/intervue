# Grader notes: Reverse Linked List

Tests cover: the sample, two nodes, one node, the empty list (`null`), repeated values (the nodes, not just the values, must come back in reverse order), the old head becoming the tail with `next == null`, values at the constraint limits, and a list of 100,000 nodes. Every list check walks the result with a node limit, so a solution that leaves the old head pointing at the second node (a two-node loop) fails instead of hanging, and every test checks node identity, so building new nodes fails.

Traps from the walkthrough and the tests that grade them:
- Forgetting to clear the old head's link: `oldHeadBecomesTheTail` and every `expect` call (the walk finds the loop).
- Recursion on a long list: a scratch recursive version passes every small test and fails `longList` with `StackOverflowError` at 100,000 nodes.

No naive solution: there is no meaningfully slower correct approach (copying the nodes into an array and relinking them backwards is also linear), so time is not graded separately for this question (ADR 0008). The 2-second limit on `longList` is generous; the reference finishes in a few milliseconds.
