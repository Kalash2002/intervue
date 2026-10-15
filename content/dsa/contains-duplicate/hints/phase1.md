## Level 1
Comparing every position with every later position works, but its cost grows with the square of the length. What do you need to know about a value when you reach it?

## Level 2
You only need to know whether you have seen that exact value earlier. Which data structure answers "is this in here?" quickly?

## Level 3
Walk the array once and keep a hash set of values seen so far. If the current value is already in the set, return true; otherwise add it. If the walk ends, return false. Sorting first and comparing neighbours also works in O(n log n) with no extra set.
