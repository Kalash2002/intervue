## Level 1
Counting the copies of each value with a scan of the array works, but every count reads the whole array again. What could you keep track of in a single pass instead?

## Level 2
Keep the values that are still waiting for their second copy: one comes in at its first copy and goes out at its second. What is left after the last value? And is there a way to keep that in a single `int` instead of a collection?

## Level 3
Exclusive or cancels a value with its copy wherever the copies are: `x ^ x` is 0, `x ^ 0` is `x`, and the order does not matter.
