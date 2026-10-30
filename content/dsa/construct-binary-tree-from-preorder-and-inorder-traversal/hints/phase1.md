## Level 1
Where in a pre-order sequence does the root of the whole tree appear, and what does its position in the in-order sequence tell you about the two subtrees?

## Level 2
The values before the root in in-order are the left subtree, so their count says how many of the next pre-order values belong to the left side. The same split then repeats inside each side. What makes finding each root in in-order slow, and how could it be fast?

## Level 3
Look each value's in-order position up instead of searching for it: record all the positions once, before you start building.
