## Level 1
To decide whether one node is good, what do you need to know about the nodes above it?

## Level 2
Comparing with the parent is not enough: in `[10,2,null,5]` the 5 beats its parent 2 but not the 10 above it. Which single number about the path above settles the question?

## Level 3
Carry the largest value seen so far on the way down; start it from the root's own value rather than from zero, because values can be negative.
