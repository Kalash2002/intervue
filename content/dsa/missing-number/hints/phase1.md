## Level 1
Checking each number from 0 to n with a scan of the array works, but every check reads the whole array again. What could you note down in a single pass so that every number can be checked at once?

## Level 2
A table of flags, one for each number from 0 to n, shows after one pass which number was never seen. Can you get the same answer without the table, by combining the numbers 0 to n and the values of the array into a single `int`?

## Level 3
List the numbers 0 to n next to the array: every number that is present now appears twice, and the missing one once.
