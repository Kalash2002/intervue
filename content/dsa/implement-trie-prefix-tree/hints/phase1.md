## Level 1
Storing the words in a list and checking each one on every query is slow when there are many words. Words that start the same way can share their first letters. How would you draw that?

## Level 2
Think of a tree where each edge is one letter and each node stands for the text spelled on the path from the root. A lookup walks one node per letter. What extra fact must a node record so that "tra" can be a prefix without being a word?

## Level 3
Mark the node where an inserted word ends. `search` must finish its walk on a marked node, while `startsWith` only needs its walk to finish.
