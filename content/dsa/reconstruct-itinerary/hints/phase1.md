## Level 1
Always flying to the alphabetically smallest destination available looks right, but try it on Example 2. Where does it go wrong, and what kind of airport causes the trouble?

## Level 2
Trying the smallest destination first and undoing a flight when you get stuck is correct, but in the worst case it explores a huge number of orders before it finds the one that works. Notice that the first time a greedy walk gets stuck, it can only be at one airport: the place where the whole trip must end.

## Level 3
Fly greedily, smallest destination first, and when you are stuck, that airport belongs at the end of whatever trip is still unwritten: build the answer back to front.
