## Level 1
Because the array is sorted, comparing the target with one element tells you about a whole side of the array. What does it tell you when the middle element is smaller than the target?

## Level 2
Think of the answer as a boundary: everything left of it is smaller than the target, everything from it onwards is not. You are looking for that boundary, so you can keep a range that always contains it and shrink the range by half each time.

## Level 3
Decide what to do when the middle element is equal to the target. You want the first equal element, so ask which side of the middle must stay in your range for that element not to be lost.
