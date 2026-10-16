# Grader notes: Linked List Cycle

Tests cover: the sample, repeated values with and without a cycle (`[4,4,4]` and `[2,7,2,7,2,7]`), one node linking to itself, one node without a cycle, the empty list, every shape of a two-node list (no cycle, back to the head, a self-loop on the tail), a cycle back to the head, a check that every value and link is unchanged after the call, and lists of 100,000 nodes holding only the values 0, 1 and 2: one without a cycle, one whose tail links back to the head and one whose tail links to itself.

Traps from the walkthrough and the tests that grade them:
- Remembering values instead of nodes: a scratch version with a `HashSet<Integer>` of values fails `repeatedValuesAreNotACycle` and `longListWithoutACycle`.
- Marking visited nodes by overwriting their values: a scratch version that writes `Integer.MIN_VALUE` into each node fails `listIsLeftUnchanged`.

The walkthrough's Better (a `HashSet<ListNode>` of visited nodes) is meant to pass: a scratch version takes 33 ms and 90 ms on the two long tests.

The naive solution checks, for each node, whether its `next` is one of the nodes visited so far by walking from the head, O(n²). Measured with a cold JVM (limit 500 ms on each long test): reference 4 to 5 ms and 3 ms over three runs (100x under the limit); naive 7.1 s on `longListWithoutACycle` and 15.5 s on `longListWhoseTailLinksBack` when given 100x the limit (both over 4.5x past the 3x line of 1,500 ms). The reference uses slow and fast pointers.
