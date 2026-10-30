## Level 1
Reaching the last node of a singly linked list means walking the whole list. Doing that once per node is too slow for 200,000 nodes; what would let you take nodes from the back cheaply?

## Level 2
The result is the first half of the list interleaved with the second half read backwards. How do you find where the second half starts without counting first?

## Level 3
Once the second half is cut off and reversed, its nodes come in exactly the order you need to weave between the nodes of the first half.
