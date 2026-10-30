# Search a 2D Matrix

You are given a grid of integers `matrix` with `m` rows and `n` columns. Read row by row, top to bottom, and left to right within each row, its values are strictly increasing: each row is sorted, and each row starts above the last value of the row before it. Return whether `target` occurs in the grid.

Implement `MatrixSearch.contains(int[][] matrix, int target)` in `MatrixSearch.java`. The tests call it many times on a grid of a million cells, so looking at every cell will be too slow.

## Example 1

**Input:** `matrix = [[2, 5, 9, 14], [20, 23, 31, 38], [44, 50, 57, 63]]`, `target = 31`
**Output:** `true`
**Explanation:** 31 is in the second row, third column.

## Example 2

**Input:** `matrix = [[2, 5, 9, 14], [20, 23, 31, 38], [44, 50, 57, 63]]`, `target = 40`
**Output:** `false`
**Explanation:** 40 would come after 38 at the end of the second row and before 44 at the start of the third, but it is not there.

## Example 3

**Input:** `matrix = [[-8], [-3], [6]]`, `target = 0`
**Output:** `false`
**Explanation:** A grid of one column is still read top to bottom; 0 falls between -3 and 6.

## Constraints

- `1 <= m <= 1000`
- `1 <= n <= 1000`
- `matrix[i].length == n` for every row `i`
- `-10^9 <= matrix[i][j], target <= 10^9`
- Read row by row, the values of `matrix` are strictly increasing.

## Follow-up

Can you answer each call in O(log(m * n)) time?
