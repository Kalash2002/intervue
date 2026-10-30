## Level 1
The median splits all the values into a lower half and an upper half. If you knew how many values of the lower half come from `a`, how many would have to come from `b`?

## Level 2
Choosing how many values to take from the front of `a` fixes how many to take from the front of `b`. The choice is right exactly when everything taken is at most everything left behind, and only two comparisons across the arrays can fail.

## Level 3
When the choice is wrong, those two comparisons tell you whether to take more or fewer values from `a`, so the number to take can be found by halving.
