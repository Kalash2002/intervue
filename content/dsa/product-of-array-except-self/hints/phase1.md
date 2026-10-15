## Level 1
Start with the obvious way: for each position, multiply all the others. How long does that take, and where is the repeated work?

## Level 2
The product of everything except position `i` can be split into two parts: the numbers before `i` and the numbers after `i`. Does the part before `i` for the next position build on the part before `i` for this one?

## Level 3
Make one sweep from the left that remembers the product of everything seen so far, and one sweep from the right that does the same. How do the two sweeps combine into the answer for each slot?
