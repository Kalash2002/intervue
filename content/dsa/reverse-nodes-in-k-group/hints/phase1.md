## Level 1
Reversing one whole list is a single walk with a `prev` and a `cur`. Here the same walk runs on one group at a time. Before you reverse a group, what do you need to know about the nodes ahead?

## Level 2
After a group is reversed, its old first node is its new last node, and two links have to be fixed: the one coming into the group and the one leaving it. Which nodes do you need in hand to fix both?

## Level 3
Keep a pointer to the node just before the current group; a dummy node in front of the head plays that role for the first group.
