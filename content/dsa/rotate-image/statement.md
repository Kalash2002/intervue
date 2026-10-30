# Rotate Image

A tile puzzle shows a square board of numbered tiles, stored as an `n x n` grid of integers. When the player turns the board a quarter turn clockwise, the top row becomes the right-hand column, the right-hand column becomes the bottom row, the bottom row becomes the left-hand column, and the left-hand column becomes the top row.

Implement `Board.rotate(int[][] grid)` in `Board.java`. Turn `grid` a quarter turn clockwise in place: when the method returns, the array you were given must hold the turned board. The method returns nothing (`void`), so a turned copy that the caller never sees does not count.

## Example 1

**Input:** `grid = [[8,3,15,22,6],[11,40,2,9,17],[5,13,0,31,24],[19,7,28,4,12],[33,21,10,16,1]]`
**Output:** `grid = [[33,19,5,11,8],[21,7,13,40,3],[10,28,0,2,15],[16,4,31,9,22],[1,12,24,17,6]]`
**Explanation:** The left-hand column read from the bottom up, 33, 19, 5, 11, 8, becomes the top row, and the top row, 8, 3, 15, 22, 6, becomes the right-hand column. The centre tile, 0, stays where it is.

## Example 2

**Input:** `grid = [[6,-1],[4,9]]`
**Output:** `grid = [[4,6],[9,-1]]`
**Explanation:** Each tile moves one corner clockwise: 6 from the top left to the top right, -1 to the bottom right, 9 to the bottom left and 4 to the top left.

## Example 3

**Input:** `grid = [[-7]]`
**Output:** `grid = [[-7]]`
**Explanation:** A board of one tile turns onto itself, so nothing changes.

## Constraints

- `n == grid.length == grid[i].length`
- `1 <= n <= 20`
- `-1000 <= grid[i][j] <= 1000`

## Follow-up

Can you do it with only a constant amount of extra memory?
