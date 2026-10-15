## Level 1
Because the array is sorted, comparing the target with one element tells you about a whole side of the array. What does it tell you when the middle element is smaller than the target?

## Level 2
Think of the answer as a boundary: everything left of it is smaller than the target, everything from it onwards is not. You are looking for that boundary, so you can keep a range that always contains it and shrink the range by half each time.

## Level 3
Keep a half-open range from `low` to `high` that starts as the whole array. Look at the middle: if it is smaller than the target, the boundary is after it, otherwise the middle itself could be the boundary. Stop when the range is empty and return `low`. Equal elements need no special case.
