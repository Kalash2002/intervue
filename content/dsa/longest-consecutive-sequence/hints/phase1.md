## Level 1
Sorting the array makes runs easy to see. What does that cost, and what happens to the repeated values? Is there a way to avoid sorting at all?

## Level 2
A run begins at a number whose predecessor is not in the array. If you can tell in constant time whether a number is present, which numbers are worth starting a count from?

## Level 3
Only a value whose predecessor is missing can be the start of a run, so only those need a count. Think about why counting only from them touches every value a bounded number of times.
