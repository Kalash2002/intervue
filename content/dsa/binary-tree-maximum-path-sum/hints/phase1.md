## Level 1
Every path has one highest node, where it turns from going up to going down. If you knew the best path going straight down from each child of that node, what is the best path that turns there?

## Level 2
Working out the best downward path from scratch for every node repeats work: a node's best downward path is built from its children's. Can one bottom-up pass hand each node what it needs from below?

## Level 3
What a node hands up to its parent and what it offers as a candidate answer are two different numbers: the parent can extend only one side, but the path that turns at the node may use both.
