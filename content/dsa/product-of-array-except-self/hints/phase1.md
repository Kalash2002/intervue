## Level 1
Start with the obvious way: for each position, multiply all the others. How long does that take, and where is the repeated work?

## Level 2
The product of everything except position `i` can be split into two parts: the numbers before `i` and the numbers after `i`. Does the part before `i` for the next position build on the part before `i` for this one?

## Level 3
For each position the answer is the product of everything to its left times the product of everything to its right, and each of those two products grows by one multiplication as you move one slot.
