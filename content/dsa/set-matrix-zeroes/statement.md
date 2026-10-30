# Set Matrix Zeroes

A monitoring wall is a grid of sensors, stored as an `m x n` grid of integer readings. A reading of 0 means that sensor has failed. The sensors of one row share a cable, and so do the sensors of one column, so every reading in the row and in the column of a failed sensor is untrustworthy and must be set to 0. Only the sensors that read 0 before you start have failed: a reading you set to 0 does not clear its own row and column.

Implement `SensorGrid.clearFailed(int[][] grid)` in `SensorGrid.java`. Change `grid` in place: when the method returns, the array you were given must hold the cleared readings. The method returns nothing (`void`), so a cleared copy that the caller never sees does not count.

## Example 1

**Input:** `grid = [[7,12,5,9,3],[4,0,8,6,11],[2,14,10,0,1],[13,6,15,8,5]]`
**Output:** `grid = [[7,0,5,0,3],[0,0,0,0,0],[0,0,0,0,0],[13,0,15,0,5]]`
**Explanation:** The failed sensors sit in rows 1 and 2 and in columns 1 and 3, so those rows and columns are cleared. The readings in rows 0 and 3 and columns 0, 2 and 4 stay.

## Example 2

**Input:** `grid = [[3,0,6,2,5],[8,4,1,9,7]]`
**Output:** `grid = [[0,0,0,0,0],[8,0,1,9,7]]`
**Explanation:** The 6, 2 and 5 are cleared because they share the top row with the failed sensor. They were not 0 at the start, so their columns keep the readings 1, 9 and 7.

## Example 3

**Input:** `grid = [[0,4],[5,-1],[6,-2147483648],[7,2147483647]]`
**Output:** `grid = [[0,0],[0,-1],[0,-2147483648],[0,2147483647]]`
**Explanation:** The only failed sensor is in the top-left corner, so the top row and the left column are cleared. The other readings stay, including -1 and the smallest and largest `int`: any value can be a real reading.

## Constraints

- `m == grid.length`
- `n == grid[i].length`
- `1 <= m, n <= 1000`
- `-2^31 <= grid[i][j] <= 2^31 - 1`

## Follow-up

Can you do it with only a constant amount of extra memory?
