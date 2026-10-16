## Level 1
Finding a key's value fast is a hash map's job. The other half is order: at any moment you must know which key was used least recently. What does keeping the keys in order of use cost on every `get`?

## Level 2
A use moves one key from somewhere in the middle of the order to the most recent end, and eviction takes from the other end. Which sequence lets you remove an element from the middle in O(1), given a direct reference to it?

## Level 3
Let the map's value be the node of a doubly linked list, so a lookup hands you the exact node to move.
