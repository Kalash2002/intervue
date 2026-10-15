## Level 1

Ask whether the cheapest way to reach a middle node is always useful. What if it already uses too many corridors to continue within the limit?

## Level 2

Plain cheapest-path search by fee is wrong here, because it keeps only one best way to reach each node and that way may spend the hop budget. Track cost per number of hops used: either repeat a relaxation over all corridors once per allowed hop, or search over pairs of node and hops used.

## Level 3

1. Handle the edge cases first: a negative limit, an unknown node, and a route from a node to itself.
2. Keep the best fee for each node after 0, 1, 2 … up to the limit of corridors.
3. In each round, read only the fees from the previous round, never the ones you are writing in this round; otherwise a round can sneak in an extra hop.
4. Use active corridors only, and allow several corridors between the same two nodes.
5. When two routes cost the same, prefer the one with fewer corridors, and rebuild the route from the round where the best fee first appeared.
