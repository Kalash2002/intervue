## Level 1

`MerchantDirectory.find` is a hand-written binary search. Look at the loop condition together with how `low` and `high` move, and try it by hand on a directory with one merchant.

## Level 2

`high` is treated as inclusive when it moves, but the loop stops as soon as `low` meets `high`, so the last remaining candidate is never compared. Names whose search narrows down to a single position are missed, which is why only some merchants fail.

## Level 3

1. Decide whether `high` is inclusive or exclusive, and make the loop condition and both updates agree with that choice.
2. Alternatively, use the library binary search the way `add` already does.
3. Run Phase 1 tests and `Main`; Zomato should now be found.
4. Be ready to explain the cost of a lookup: the range halves each step, so it grows with the logarithm of the directory size.
