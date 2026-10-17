## Level 1
The trace can start on any cell holding the first letter. From there, which cells could hold the second letter, and what happens to the search when none of them does?

## Level 2
Try the neighbours one at a time and go deeper only while the letters keep matching. A cell already on the current trace must not be stepped on again. What has to happen to that cell when the search gives up on it and backs out?

## Level 3
Let the grid itself remember the current trace: overwrite a cell when you step onto it, and put its letter back when you step off.
