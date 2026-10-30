## Level 1
For each open cell you could search its region for a border cell, but a large enclosed region is then searched once per cell. Which open cells are easy to classify at a glance?

## Level 2
An open cell on the border is never filled, and neither is anything connected to it. If you knew every open cell connected to the border, what would the remaining open cells be?

## Level 3
Search inwards from the open border cells and mark what you reach as safe; afterwards every open cell left unmarked is surrounded.
