# Trapping Rain Water

A row of bars of width 1 stands on flat ground, and `heights[i]` is the height of the bar at position `i`. After a heavy rain, water collects in the dips between the bars. Water at a position stays only if there is a bar at least as high on the left and a bar at least as high on the right of it; at the two ends of the row water runs off.

Implement `Basin.trapped(int[] heights)` in `Basin.java`. Return the total amount of water, in square units, that the row holds.

## Examples

- `heights = [3, 0, 2, 0, 4]` returns `7`: the dips hold 3, 1 and 3 units, up to the lower of the two tallest bars around each.
- `heights = [4, 1, 1, 4]` returns `6`: a flat basin two wide and three deep.
- `heights = [1, 2, 3]` returns `0`: heights only go up, so nothing can be held.

## Constraints

- 0 <= `heights.length` <= 100,000
- 0 <= `heights[i]` <= 10,000
- The answer always fits in a 32-bit `int`.
