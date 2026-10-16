## Level 1
The array is sorted. If you compare the target with one value in the middle, what do you learn about all the values on one side of it?

## Level 2
Keep a range of indexes where the target could still be. Each comparison with the middle of that range either finds the target or rules out half of the range.

## Level 3
Decide whether the range's upper end is included or not, and keep every update consistent with that choice; most wrong answers here come from mixing the two.
