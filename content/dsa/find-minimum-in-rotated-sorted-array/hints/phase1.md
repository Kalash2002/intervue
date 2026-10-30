## Level 1
A rotated sorted array is two increasing runs, one after the other. Where is the smallest value relative to those runs?

## Level 2
Every value of the first run is larger than every value of the second. Comparing the middle value with one fixed end of the current range tells you which run the middle is in, and so which half still holds the smallest value.

## Level 3
Compare the middle with the last value of the range rather than the first: that comparison stays correct when the array was not rotated at all.
