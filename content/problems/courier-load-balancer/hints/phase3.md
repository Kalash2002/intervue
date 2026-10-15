## Level 1

For each parcel, the code looks at every courier to find the lightest. Multiply the number of parcels by the number of couriers in the tests and compare that with what a computer does in a few seconds.

## Level 2

A priority queue keeps the lightest courier on top: you take it, add the parcel, and put it back, in logarithmic time. To keep the "listed first" tie-break, order the queue by load and then by the courier's position in the list.

## Level 3

1. Put one entry per courier in a `PriorityQueue`, holding its current load and its index.
2. Order the queue by load, then by index, so equal loads come out in list order.
3. For each parcel, look at the top entry; with a capacity, leave the parcel over if it does not fit and do not touch the queue.
4. Otherwise remove the top, add the weight, give the parcel to that courier and put the entry back.
5. Run Phase 3 tests and check the plans still match phases 1 and 2.
