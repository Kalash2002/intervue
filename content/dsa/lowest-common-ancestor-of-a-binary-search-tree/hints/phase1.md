## Level 1
Standing at a node, can you tell from the values alone whether `p` and `q` are both on the left, both on the right, or on different sides?

## Level 2
If both values are smaller than the node, the answer is somewhere in the left subtree; if both are larger, in the right. What does it mean when neither is true?

## Level 3
The first node on the way down whose value is not strictly beyond both `p` and `q` on the same side is the answer; compare each value with the node separately rather than combining the two differences into one number.
