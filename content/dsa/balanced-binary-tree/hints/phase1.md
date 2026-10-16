## Level 1
Checking only the root is not enough: the condition holds at every node. What does a node need to know about each of its two subtrees?

## Level 2
A node needs both subtree heights and needs both subtrees to be balanced themselves. Asking those as two separate questions makes every node below be measured again and again.

## Level 3
Let the height computation also carry the bad news: once any subtree is out of balance, return a value no real height can have.
