# Car Fleet

Several cars drive along a one-lane road towards a gate at distance `gate` from the start of the road. Car `i` begins at position `start[i]` (no two cars begin at the same position, and every start is before the gate) and drives at the constant speed `speed[i]`.

A car cannot overtake. If a faster car comes up behind a slower one it slows to the slower car's speed and from then on they drive together as one group. A car that would reach the gate at the same moment as the group ahead of it, even only exactly at the gate, counts as part of that group. A single car on its own is also a group.

Implement `Convoy.groups(int gate, int[] start, int[] speed)` in `Convoy.java`. Return the number of groups that arrive at the gate. The cars are not given in any particular order.

## Examples

- `gate = 20`, `start = [2, 6, 9, 13]`, `speed = [3, 2, 1, 4]` returns `2`: the car at 13 gets there alone, and the cars at 9, 6 and 2 all end up behind the slow car at 9 and arrive together.
- `gate = 12`, `start = [0, 4, 8]`, `speed = [1, 2, 4]` returns `3`: each car is faster than the one behind it, so nobody ever catches up.
- `gate = 10`, `start = [4, 6]`, `speed = [3, 2]` returns `1`: the car at 4 needs 2 time units, and so does the car at 6, so they meet exactly at the gate.

## Constraints

- 0 <= `start.length` = `speed.length` <= 100,000
- 1 <= `gate` <= 1,000,000
- 0 <= `start[i]` < `gate`, and all start positions are different
- 1 <= `speed[i]` <= 1,000,000
