## Level 1
A stretch either covers the needed letters or it does not. If a stretch covers them, what happens to that when you extend it to the right? And when you cut letters off the left?

## Level 2
Checking every start and end is slow. Once a stretch covers the need, you want it as short as possible, and the right end is already fixed by how you got there. What can you do from the left side to make it shorter, and when must you stop?

## Level 3
Keep counts of what is still missing. Move the right end until nothing is missing, then move the left end while nothing becomes missing again, recording the shortest stretch seen. Then let the left end go one step further so something is missing, and carry on with the right end.
