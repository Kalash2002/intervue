## Level 1

The two workloads are slow for different reasons. Read `Datasets` and the two tests, then time each workload on its own. One of them barely touches the code you wrote in phase 2.

## Level 2

The single busy account needs the phase 2 calculation to grow like n log n, not n squared. The many small accounts are slow because of how `HoldBook` finds holds: looking up one account's holds and releasing a hold both scan every hold in the book.

## Level 3

1. Make sure `peak` and `firstBreach` sort once and sweep once, with no nested loop over holds.
2. In `HoldBook`, keep the holds grouped by account so that one account's holds are found directly.
3. Keep a way to find a hold by its id so `release` does not search the whole book.
4. Keep `accounts()` returning every account that ever had a hold, even after its last hold is released; the tests count them.
5. Run Phase 3 tests, then all tests, to check nothing in phases 1 and 2 broke.
