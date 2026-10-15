## Level 1
Storing the words in a list and checking each one on every query is slow when there are many words. Words that start the same way can share their first letters. How would you draw that?

## Level 2
Think of a tree where each edge is one letter and each node stands for the text spelled on the path from the root. A lookup walks one node per letter. What extra fact must a node record so that "tra" can be a prefix without being a word?

## Level 3
Give each node 26 child slots, one per letter, and a flag that says a word ends here. To insert, walk or create child nodes along the letters and set the flag on the last node. A search walks the letters and succeeds only if the walk completes on a node with the flag set; startsWith succeeds if the walk completes at all.
