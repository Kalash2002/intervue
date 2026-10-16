## Level 1
Remembering which values you have seen solves it, but takes memory proportional to `n`, and sorting or marking would write to the array. What else can you do with values that are all valid positions in the array?

## Level 2
Every value is between 1 and `n`, so it can be used as an index. Starting at index 0, keep jumping to the index the current value names. What shape does that walk trace?

## Level 3
The walk ends in a loop, and the loop is entered at the value that two different positions point to.
