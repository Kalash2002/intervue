## Level 1
In which order would you have to visit the nodes of a binary search tree to meet the values from smallest to largest?

## Level 2
Left subtree, then the node, then the right subtree gives the values in increasing order. Do you need to finish that walk to know the `k`-th value?

## Level 3
The in-order walk knows the answer the moment it has met `k` values; make it a loop you can leave.
