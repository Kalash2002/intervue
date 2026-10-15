## Level 1

Capacity changes only one decision: whether the chosen courier takes the parcel. Everything about which courier is chosen stays the same as in `balance`.

## Level 2

Keep the same order of parcels and the same choice of the lightest courier. Before giving the parcel, check that the courier's load plus the parcel is at most the capacity; if not, record the parcel as left over and move on without changing any load.

## Level 3

1. Reject a capacity that is zero or negative first.
2. Share the loop with `balance` if you can, passing a capacity that can never be reached for the plain version.
3. After choosing the lightest courier, compare the new load with the capacity using at-most.
4. Call `leaveOver` for parcels that do not fit and continue with the next parcel.
5. Run Phase 2 tests and walk the first test by hand: p5 is left over even though p3 later fits.
