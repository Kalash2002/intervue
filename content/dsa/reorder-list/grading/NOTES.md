# Grader notes: Reorder List

Tests cover: the sample (odd length, the middle node ends the list), even lengths (4 and 6), two nodes (nothing moves), one node, three nodes, repeated values (the nodes, not the values, must move), every length from 1 to 12 with the expected order worked out from the index rule (position k holds original node k/2 for even k and n - 1 - k/2 for odd k), and a list of 200,000 nodes. Every check walks the list with a node limit, so a list that loops back fails instead of hanging, and checks node identity, so rewriting values fails.

Traps from the walkthrough and the tests that grade them:
- Not cutting the first half off the second: a scratch version without `slow.next = null` fails every test with two or more nodes (the walk finds the loop).
- Splitting so the second half is longer than the first: a scratch version that gives the first half floor(n/2) nodes drops the last node on odd lengths and fails `sampleOddLength`, `threeNodes` and `everyLengthFromOneToTwelve`.
- Swapping values instead of nodes: a scratch version fails every test where any node moves, on the identity check.

The naive solution walks to the second-to-last node once per step, O(n²). Measured with a cold JVM on `longList` (limit 1,500 ms): reference 87, 89 and 103 ms over three runs (best 87 ms, 17x under the limit), naive 27.6 s when given 100x the limit (18x the limit, over 6x past the 3x line of 4,500 ms). The reference finds the middle with slow and fast pointers, reverses the second half and interleaves.
