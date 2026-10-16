# Container With Most Water

Along a line stand vertical walls, one at each position `0, 1, 2, ...`. The wall at position `i` has height `heights[i]`. If you choose two walls, they form a tank: its width is the distance between the two positions, and its water level is the height of the shorter of the two walls. Walls in between are ignored. The volume is width times level.

Implement `Containers.maxVolume(int[] heights)` in `Containers.java`. Return the largest volume that any pair of walls can hold.

## Example 1

**Input:** `heights = [3, 9, 2, 8, 4, 7, 1]`
**Output:** `28`
**Explanation:** The walls at positions 1 and 5 have heights 9 and 7, so the level is 7 and the width is 4, giving 28. No other pair holds more.

## Example 2

**Input:** `heights = [5, 6]`
**Output:** `5`
**Explanation:** There is only one pair: width 1 and level 5.

## Example 3

**Input:** `heights = [4, 0, 0, 4]`
**Output:** `12`
**Explanation:** The two outer walls are 3 apart, and the empty space between them does not matter: width 3 times level 4.

## Constraints

- `2 <= heights.length <= 200000`
- `0 <= heights[i] <= 10000`
- The answer fits in a 32-bit signed integer.

## Follow-up

Can you do it in O(n) time with O(1) extra memory?
