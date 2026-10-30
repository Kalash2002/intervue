## Level 1
Neither always stepping to the lowest higher neighbour nor always to the highest finds the 9-cell climb in Example 1, so every way up has to be considered. Trying them all from every cell is correct, but count how often two climbs from different starts reach the same cell and then search everything above it again.

## Level 2
The longest climb from a cell does not depend on how the hiker got there: it is the cell itself plus the longest climb from its best higher neighbour, or just the cell when nothing beside it is higher. Each cell has one such number, and it never changes once it is known.

## Level 3
That rule only ever looks at higher cells, so the cells with nothing higher beside them can be settled first, and the rest in order from there, without any recursion.
