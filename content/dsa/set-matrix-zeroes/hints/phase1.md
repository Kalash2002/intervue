## Level 1
Clearing a row and a column the moment you meet a 0 writes new zeros that look exactly like failed sensors. What do you need to know about the grid before you write anything?

## Level 2
The answer depends only on which rows hold a 0 and which columns hold a 0: m + n yes-or-no facts, found in one pass. Where could you keep them without allocating anything the size of the grid?

## Level 3
A failing row's cell in the first column ends up 0 anyway, and so does a failing column's cell in the first row, so those cells can carry the flags.
