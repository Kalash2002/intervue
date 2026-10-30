## Level 1
Add 19 and 45 in binary on paper, column by column from the right. In each column, what decides the sum bit, and what decides whether a carry goes into the next column? Which bitwise operations give those two from the column's bits?

## Level 2
Instead of one column at a time, work on all 32 at once: one bitwise operation gives every column's sum bit as if there were no carries, and another marks every column that makes a carry. What is still left to add after that?

## Level 3
The carries are just another number to add to that partial sum, and each time round they start at least one column further left.
