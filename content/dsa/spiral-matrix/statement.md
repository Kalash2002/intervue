# Spiral Matrix

A robot vacuum cleans a rectangular floor of tiles, stored as an `m x n` grid with one dust reading per tile. It starts on the top-left tile and moves right along the top row. Whenever the next tile is off the floor or already cleaned, it turns clockwise (right, then down, then left, then up, then right again), and it stops when every tile is cleaned, so it spirals in from the edge.

Implement `Sweep.spiralOrder(int[][] grid)` in `Sweep.java`. Return the readings in the order the robot cleans the tiles, as a `List<Integer>`. Do not modify `grid`.

## Example 1

**Input:** `grid = [[12,7,30,4,18],[25,9,41,16,2],[6,33,14,27,11],[19,1,22,8,35]]`
**Output:** `[12,7,30,4,18,2,11,35,8,22,1,19,6,25,9,41,16,27,14,33]`
**Explanation:** The outer ring gives 12 to 18 along the top, 2, 11, 35 down the right, 8, 22, 1, 19 back along the bottom and 6, 25 up the left. The 2 x 3 block inside gives 9, 41, 16, then 27 down, then 14, 33 back.

## Example 2

**Input:** `grid = [[3,-4],[10,6],[-8,15],[2,0],[7,-1]]`
**Output:** `[3,-4,6,15,0,-1,7,2,-8,10]`
**Explanation:** The floor is two tiles wide: across the top, down the right-hand column, one tile left along the bottom, and up the left-hand column, which ends next to the start.

## Example 3

**Input:** `grid = [[4],[-2],[9]]`
**Output:** `[4,-2,9]`
**Explanation:** On a single column the robot cannot move right, so it turns and goes straight down. Every tile below it is then cleaned, so it must not come back up.

## Constraints

- `m == grid.length`
- `n == grid[i].length`
- `1 <= m <= 100`
- `1 <= n <= 100`
- `-1000 <= grid[i][j] <= 1000`

## Follow-up

How would you produce the spiral one reading at a time, for a caller that may stop early?
