## Level 1
Mirroring the whole tree sounds big. What does it mean for a single node, looking only at its two children?

## Level 2
Once a node's children are swapped, the two subtrees hanging from it still need mirroring too. Is that the same problem again, only smaller?

## Level 3
Swap the two children at a node and then mirror each subtree the same way; just do not lose one child while you overwrite the other.
