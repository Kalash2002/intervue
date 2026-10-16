# Car Fleet

Several cars drive along a one-lane road towards a gate at distance `gate` from the start of the road. Car `i` begins at position `start[i]` and drives at the constant speed `speed[i]`. The cars are not given in any particular order.

A car cannot overtake. If a faster car comes up behind a slower one it slows to the slower car's speed, and from then on they drive together as one group. A car that would reach the gate at the same moment as the group ahead of it, even only exactly at the gate, counts as part of that group. A single car on its own is also a group. Arrival times are not always whole numbers.

Implement `Convoy.groups(int gate, int[] start, int[] speed)` in `Convoy.java`. Return the number of groups that arrive at the gate.

## Example 1

**Input:** `gate = 20`, `start = [2, 6, 9, 13]`, `speed = [3, 2, 1, 4]`
**Output:** `2`
**Explanation:** The car at 13 gets there alone. The cars at 9, 6 and 2 all end up behind the slow car at 9 and arrive together.

## Example 2

**Input:** `gate = 12`, `start = [8, 0, 4]`, `speed = [4, 1, 2]`
**Output:** `3`
**Explanation:** Each car is faster than the one behind it, so nobody ever catches up and every car is its own group.

## Example 3

**Input:** `gate = 30`, `start = [5, 9]`, `speed = [10, 10]`
**Output:** `2`
**Explanation:** The car at 9 needs 2.1 time units and the car at 5 needs 2.5. They drive at the same speed, so the car at 5 never catches up, and rounding both times to 2 would wrongly merge them.

## Example 4

**Input:** `gate = 10`, `start = [4, 6]`, `speed = [3, 2]`
**Output:** `1`
**Explanation:** The car at 4 needs 2 time units, and so does the car at 6, so they meet exactly at the gate and count as one group.

## Constraints

- `0 <= start.length <= 10^5`
- `speed.length == start.length`
- `1 <= gate <= 10^6`
- `0 <= start[i] < gate`
- `1 <= speed[i] <= 10^6`
- All start positions are different.

## Follow-up

Can you beat O(n²) time, where n is `start.length`?
