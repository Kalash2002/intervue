## Level 1
You never need the actual distance, only which of two points is closer. What can you compare instead of a square root, and how big can that number get with these coordinates?

## Level 2
Sorting every point by distance works, but it orders far more than you need: you want only the first k, and in any order. Can you keep just k candidates as you read the points, and know at once which one to throw out?

## Level 3
If you keep the k closest so far, the only one that matters when a new point arrives is the farthest of them.
