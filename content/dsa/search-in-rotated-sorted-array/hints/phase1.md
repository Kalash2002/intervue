## Level 1
Split the array at any index. At least one of the two pieces has no drop in it. How can you tell which one, from its two end values?

## Level 2
A piece with no drop is sorted, so one comparison with each of its ends tells you whether the target can be inside it. If it cannot, the target can only be in the other piece.

## Level 3
When the range has two values, the middle is the range's first index, so the left piece is a single value; make sure your test for "the left piece is sorted" says yes in that case.
