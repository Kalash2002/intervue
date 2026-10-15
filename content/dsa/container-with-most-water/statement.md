# Container With Most Water

Along a line stand vertical walls, one at each position `0, 1, 2, ...`. The wall at position `i` has height `heights[i]`. If you choose two walls, they form a tank: its width is the distance between the two positions, and its water level is the height of the shorter of the two walls. Walls in between are ignored. The volume is width times level.

Implement `Containers.maxVolume(int[] heights)` in `Containers.java`. Return the largest volume that any pair of walls can hold.

## Examples

- `heights = [3, 9, 2, 8, 4, 7, 1]` returns `28`: the walls at positions 1 and 5 have heights 9 and 7, so the level is 7 and the width is 4.
- `heights = [5, 6]` returns `5`: width 1 and level 5.
- `heights = [4, 0, 0, 4]` returns `12`: the two outer walls are 3 apart, and the empty space between them does not matter.

## Constraints

- 2 <= `heights.length` <= 200,000
- 0 <= `heights[i]` <= 10,000
- The answer always fits in a 32-bit `int`.
