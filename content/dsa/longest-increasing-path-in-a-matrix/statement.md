# Longest Increasing Path in a Matrix

A hiker crosses a map laid out as a grid of land heights. From a cell she can step to any of its four side neighbours, up, down, left or right, but never diagonally and never off the map. She only ever climbs: every step must go to a strictly higher cell. She may start on any cell.

Implement `Hike.longestClimb(int[][] heights)` in `Hike.java` and return the number of cells on the longest such climb, counting the cell it starts on. A cell on its own is a climb of one cell.

## Example 1

**Input:** `heights = [[5, 6, 7, 2], [4, 9, 8, 1], [3, 2, 0, 1]]`
**Output:** `9`
**Explanation:** The climb 0, 2, 3, 4, 5, 6, 7, 8, 9 starts on the 0 in the bottom row, goes left along that row, up the left column and right along the top, then down to the 8 and left onto the 9 in the middle. Always stepping to the lowest higher neighbour would go from the 0 to the 1 beside it and stop there.

## Example 2

**Input:** `heights = [[8, 1, 2, 3, 4], [0, 9, 9, 9, 5]]`
**Output:** `6`
**Explanation:** 1, 2, 3 and 4 along the top, down to the 5, then left onto a 9. The lowest cell, the 0, only starts climbs of two cells: the step from it to the 1 is diagonal, and the three 9s are level with each other.

## Example 3

**Input:** `heights = [[4, 4], [4, 4], [4, 4]]`
**Output:** `1`
**Explanation:** Equal heights are not a climb, so no cell has a neighbour to step to and every climb is a single cell.

## Constraints

- `1 <= heights.length <= 50`
- `1 <= heights[r].length <= 50`
- All rows have the same length.
- `0 <= heights[r][c] <= 2^31 - 1`

## Follow-up

What if you also had to return the cells of one longest climb?
