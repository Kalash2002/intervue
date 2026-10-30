# Grader notes: Copy List with Random Pointer

`Node.java` (read-only) builds a list from `{value, randomIndex}` pairs and checks a copy with `deepCopyProblem`: same values, same `next` order, every `random` pointing at the same position, and no node or link of the copy shared with the input. Every test also checks, after the call, that each input node still has its value, its `next` and its `random`.

Tests cover: the sample, equal values with crossed links (`[[5,1],[5,0]]` and four nodes all holding -3), the empty list, one node whose `random` is itself, no random links at all, links that point forwards to nodes not copied yet, every node pointing at the head, values at the constraint limits, and a list of 200,000 nodes with values 0 to 9 and random targets drawn with a fixed seed (one in ten `null`).

Traps from the walkthrough and the tests that grade them:
- Matching nodes by value: a scratch version with a `HashMap<Integer, Node>` from value to copy fails `equalValuesWithCrossedLinks` and `longList`.
- Weaving without putting the input back: a scratch version that splits off the copies but leaves the originals pointing at them fails every test except `emptyList`.
- Sharing nodes or links with the input (returning the input, or copying `random` as is): `deepCopyProblem` reports the shared node; the starter, which returns the input, fails every test except `emptyList`.

The walkthrough's Better (a `HashMap<Node, Node>`) is meant to pass: a scratch version takes 452 ms on `longList`.

The naive solution finds each copy's random target by walking both lists from the head, O(n²). Measured with a cold JVM (limit 3,500 ms on `longList`): reference 289, 298 and 299 ms over three runs (at most 437 = limit/8); naive 34.9 s when given 100x the limit (needs 15.75 s = 1.5 x 3 x limit; 3.3x past the 3x line of 10.5 s). The list size is 2 * 10^5 rather than the usual small size so that this gap exists; most of the reference's time is the test's own checking of 200,000 nodes. The reference weaves the copies into the list.
