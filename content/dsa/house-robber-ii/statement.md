# House Robber II

Houses stand around a circular road, numbered `0` to `n - 1` in order, and house `i` holds `cash[i]`. Each house is a neighbour of the houses on either side of it, and because the road is a circle, house `n - 1` and house `0` are neighbours too. Emptying two neighbouring houses in one night sets off an alarm.

Implement `RingRoad.maxLoot(int[] cash)` in `RingRoad.java` and return the largest total that can be taken without emptying two neighbouring houses. When there is only one house it has no neighbour and can be emptied; with two houses, they are neighbours of each other.

## Example 1

**Input:** `cash = [9,2,3,8]`
**Output:** `12`
**Explanation:** Houses 0 and 3 would give 17 on a straight street, but on the circle they are neighbours. The best is 9 + 3 from houses 0 and 2.

## Example 2

**Input:** `cash = [2,7,5,1,6,9]`
**Output:** `17`
**Explanation:** Houses 1, 3 and 5 give 7 + 1 + 9. House 5 is taken, so house 0 cannot be, and it is not.

## Example 3

**Input:** `cash = [5]`
**Output:** `5`
**Explanation:** A single house has no neighbour, so it is emptied.

## Constraints

- `1 <= cash.length <= 1000`
- `0 <= cash[i] <= 10^6`

## Follow-up

Why is it safe to split the circle into two straight streets?
