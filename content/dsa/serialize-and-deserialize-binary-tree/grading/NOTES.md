# Grader notes: Serialize and Deserialize Binary Tree

Every test serializes on one `TreeCodec`, deserializes on a fresh one, and checks three things: the text is not `null` and at most `20 * (n + 1)` characters long, the rebuilt tree has the same level order as the original, and the rebuilt root is not the original root (new nodes). The tests never read the text, so any format passes; the size rule allows comma-separated values with a `null` marker per missing child in either pre-order or level order (about 17 characters per node at worst).

Tests cover: the sample (with a repeated value), the empty tree, single nodes of 0 and at each value limit, `[7,7]` and `[7,null,7]` (which must also give different texts) and a tree of five 7s, negative and ten-digit values, two uneven shapes, a rising right path of 18 nodes (the size check), paths of 1,000 nodes leaning left, leaning right and zigzagging (the depth limit, so a recursive solution must still fit), and a tree of 100,000 nodes with repeated values: a 983-node path on top of a balanced block, depth exactly 1,000.

Traps from the walkthrough and hints, and the tests that grade them (each checked with a scratch version):
- Writing values without markers for missing children: cannot tell `[7,7]` from `[7,null,7]`, so it fails `sameValuesDifferentShapes`; a version that writes pre-order values and rebuilds by inserting into a search tree also fails `sample` and every larger test.
- Numbered slots (a node in slot i has its children in slots 2i + 1 and 2i + 2), the walkthrough's brute force: fails `sizeStaysLinear` on the size rule (524,301 characters for 18 nodes) and runs out of time on `deepestAllowedPaths` and `largeTree`.

Depth is capped at 1,000 in the constraints so a recursive solution is safe; the reference is recursive pre-order with `#` for a missing child.

No naive solution: the correct formats are all linear, and the slow one (numbered slots) is exponential in the depth and breaks the size rule rather than a time limit, so time is not graded separately for this question (ADR 0008). The 2,000 ms limit on `largeTree` only guards against quadratic string building; the reference took 126 to 143 ms there on a cold JVM, and the walkthrough's level-order version 137 ms.
