## Level 1
Build the string one bracket at a time from the left. At any point, which of the two brackets could come next without making the string impossible to finish?

## Level 2
Keep track of how many opening and closing brackets you have used. An opening bracket is fine while you have some left; when is a closing bracket fine? When both counts reach n you hold one answer.

## Level 3
A closing bracket may be added only while it has an unmatched opening bracket before it, so every prefix you build is one that can still be completed.
