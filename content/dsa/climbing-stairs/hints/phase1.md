## Level 1
Think about the very last move of any way to reach step n. Where could you have been standing just before it?

## Level 2
Every way to reach step n is a way to reach step n - 1 followed by a single step, or a way to reach step n - 2 followed by a double step, and no way is both. A plain recursion on that is correct but recomputes the same smaller staircases an enormous number of times.

## Level 3
The count for a step depends only on the counts for the two steps below it, so build the counts upwards from the floor instead of downwards from the top.
