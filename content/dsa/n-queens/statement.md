# N-Queens

A chess queen attacks every square in its row, its column and both of its diagonals, however far away. Place `n` queens on an `n` by `n` board so that no queen attacks another.

Implement `NQueens.solve(int n)` in `NQueens.java`. Return every such placement exactly once. Write each placement as a list of `n` strings, one per row from top to bottom, where `'Q'` is a queen and `'.'` is an empty square. The placements may be returned in any order; if there are none, return an empty list.

## Example 1

**Input:** `n = 6`
**Output:** `[[".Q....","...Q..",".....Q","Q.....","..Q...","....Q."], ["..Q...",".....Q",".Q....","....Q.","Q.....","...Q.."], ["...Q..","Q.....","....Q.",".Q....",".....Q","..Q..."], ["....Q.","..Q...","Q.....",".....Q","...Q..",".Q...."]]` (in any order)
**Explanation:** A 6 by 6 board has exactly four placements. In the first, the queens stand in columns 1, 3, 5, 0, 2 and 4 of rows 0 to 5, and no two share a column or a diagonal.

## Example 2

**Input:** `n = 2`
**Output:** `[]`
**Explanation:** The second queen must go in the other row, and both squares there share a column or a diagonal with the first queen, wherever it stands.

## Constraints

- `1 <= n <= 9`

## Follow-up

How would you only count the placements, as fast as possible?
