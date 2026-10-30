# Grader notes: Merge K Sorted Lists

Tests cover: the sample, an empty array, an array of only empty lists, empty lists mixed with non-empty ones (including `[[],[6,6],[]]`, where one list holds every node), a single list, equal values spread over several lists, values at the constraint limits, one long list with two one-node lists, and 50,000 lists of 4 random sorted values each (200,000 nodes, fixed seed) whose expected order the test gets by sorting all values. Every list check counts nodes against the expected total (so a loop fails instead of hanging) and checks that each node of the result is one of the given nodes, used once.

Traps from the walkthrough and hints, and the tests that grade them:
- Putting `null` heads into the heap: a scratch version fails `everyListEmpty` and `someListsEmpty` with `NullPointerException`.
- Merging the lists one by one into a growing result: a scratch version passes every small test and times out on `manyLists`.

The walkthrough's Better (merging in rounds of pairs) is meant to pass: a scratch version takes 213 ms on `manyLists`.

The naive solution scans every front for the smallest at each step, O(N k). Measured with a cold JVM (limit 2,500 ms on `manyLists`): reference 223, 233 and 243 ms over three runs (at most 312 = limit/8); naive 27.2 s when given 100x the limit (needs 11.25 s = 1.5 x 3 x limit; 3.6x past the 3x line of 7.5 s). With 10,000 lists of 20 nodes the naive took only 5.4 s while the reference stayed near 220 ms, so `k` goes up to 5 * 10^4 here. The reference keeps the fronts in a `PriorityQueue`.
