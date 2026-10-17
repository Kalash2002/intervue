## Level 1
No two queens can share a row, so every placement has exactly one queen in each row. How does that shrink the choices compared with putting n queens on any of the n² squares?

## Level 2
Place the queens row by row, and before placing one, check it against the queens already standing; if every column in a row is attacked, back up and move the queen in the row above. Which lines does a new queen have to be checked against?

## Level 3
All squares on one diagonal share the same value of row minus column, and on the other diagonal the same row plus column, so a diagonal can be marked as taken like a column.
