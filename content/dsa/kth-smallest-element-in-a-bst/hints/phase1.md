## Level 1
In which order would you have to visit the nodes of a binary search tree to meet the values from smallest to largest?

## Level 2
Left subtree, then the node, then the right subtree gives the values in increasing order. Do you need to finish that walk to know the `k`-th value?

## Level 3
Run the in-order walk with your own stack and count nodes as they come off it; the moment the count reaches `k`, you have the answer and can stop.
