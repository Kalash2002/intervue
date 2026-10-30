## Level 1
Making the new nodes and their `next` links is one easy walk. The hard part is `random`: when you copy a node, how do you find the copy of the node its `random` points at?

## Level 2
Values can repeat, so a value does not identify a node. What you need is, for every original node, a quick way to reach its copy. Where could you keep that pairing?

## Level 3
The pairing does not need a separate table: for a while, the list itself can hold each copy right next to its original.
