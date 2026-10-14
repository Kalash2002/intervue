## Level 1

The loads come out right but the wrong courier gets each parcel when loads are equal. Look at how `balance` picks the lightest courier, and at what happens when two loads are the same.

## Level 2

The search keeps the current best and replaces it when it finds a load that is less than or equal to the best one. With "or equal", a later courier with the same load wins every tie, so the last courier listed is chosen instead of the first.

## Level 3

1. Open `LoadBalancer.balance` and find the comparison inside the loop over couriers.
2. Replace it with a strict less-than, so a later courier only wins when it is strictly lighter.
3. Leave the sorting alone: heaviest first and then by id is already right.
4. Run Phase 1 tests: on the sample, ASHA should carry P1 and P6.
