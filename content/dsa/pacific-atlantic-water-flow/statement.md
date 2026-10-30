# Pacific Atlantic Water Flow

An island is mapped as a grid of land heights. The Pacific Ocean washes the top and left edges of the map, and the Atlantic Ocean washes the bottom and right edges. When rain falls on a cell, the water can run to any of the four side neighbours whose height is less than or equal to the cell's own, and from there onwards by the same rule. A cell on an edge drains straight into the ocean on that edge.

Implement `WaterFlow.bothOceans(int[][] heights)` in `WaterFlow.java`. Return the cells, as `{row, column}` pairs, from which rain can reach both oceans. The cells may be in any order.

## Example 1

**Input:** `heights = [[1, 3, 4], [2, 5, 3], [6, 2, 1]]`
**Output:** `[[0, 2], [1, 1], [2, 0]]`
**Explanation:** The top-right and bottom-left corners touch both oceans. Water on the 5 in the middle can run to the 3 above it, which is on the Pacific edge, and to the 3 on its right, which is on the Atlantic edge. Every other cell is boxed in by higher neighbours on the way to one of the oceans.

## Example 2

**Input:** `heights = [[2, 2], [2, 2]]`
**Output:** `[[0, 0], [0, 1], [1, 0], [1, 1]]`
**Explanation:** Water runs between equal heights, so every cell can reach both corners that touch both oceans.

## Example 3

**Input:** `heights = [[4, 8, 1]]`
**Output:** `[[0, 0], [0, 1], [0, 2]]`
**Explanation:** With a single row, every cell is on the top edge and on the bottom edge at once.

## Constraints

- `1 <= heights.length <= 200`
- `1 <= heights[r].length <= 200`
- All rows have the same length.
- `0 <= heights[r][c] <= 10^5`

## Follow-up

Can you do it without a separate search from every cell?
