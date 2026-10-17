# Min Cost Climbing Stairs

A staircase has steps numbered `0` to `n - 1`, and standing on step `i` costs `fee[i]`. You start on the floor, just below step 0, and want to reach the landing, just above step `n - 1`. Each move climbs one or two steps, so from the floor you can land on step 0 or step 1, and from step `n - 2` or `n - 1` you can reach the landing. The floor and the landing are free.

Implement `StairFees.minCost(int[] fee)` in `StairFees.java` and return the smallest total of fees you can pay on the way from the floor to the landing.

## Example 1

**Input:** `fee = [4,9,2,7,3]`
**Output:** `9`
**Explanation:** Stand on steps 0, 2 and 4 and pay 4 + 2 + 3. A route through step 1 pays 9 there alone, and the only other route that avoids step 1 stands on the 7.

## Example 2

**Input:** `fee = [5,1,1,8,1,6]`
**Output:** `3`
**Explanation:** Climb two from the floor to step 1, then steps 2 and 4, then two more to the landing: 1 + 1 + 1. The last step is skipped.

## Example 3

**Input:** `fee = [6,2]`
**Output:** `2`
**Explanation:** Climb two from the floor straight to step 1, pay 2, and take one step to the landing; starting on step 0 would cost 6.

## Constraints

- `2 <= fee.length <= 1000`
- `0 <= fee[i] <= 999`

## Follow-up

What if you also had to return which steps to stand on?
