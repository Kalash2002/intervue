# Surrounded Regions

A board is a grid of `'X'` and `'O'` cells. Open cells (`'O'`) that share a side belong to the same region; cells that only touch at a corner do not. A region is surrounded when none of its cells lies on the border of the board, that is, in the first or last row or column.

Implement `Capture.surround(char[][] board)` in `Capture.java`. Change the board in place so that every cell of every surrounded region becomes `'X'`. Every other cell keeps its value. The examples write each row as a string.

## Example 1

**Input:** `board = ["XXXXX", "XOOXO", "XXOXO", "XOXXX"]`
**Output:** `["XXXXX", "XXXXO", "XXXXO", "XOXXX"]`
**Explanation:** The three open cells in the middle of the left half touch no border cell, so they are filled. The pair on the right edge and the single cell on the bottom edge are on the border and stay open.

## Example 2

**Input:** `board = ["XXX", "XOX", "XXO"]`
**Output:** `["XXX", "XXX", "XXO"]`
**Explanation:** The centre cell touches the open corner cell only diagonally, which does not connect them, so the centre is filled while the corner stays open.

## Example 3

**Input:** `board = ["O"]`
**Output:** `["O"]`
**Explanation:** On a one-cell board the only cell is on the border, so nothing is surrounded.

## Constraints

- `1 <= board.length <= 200`
- `1 <= board[i].length <= 200`
- All rows have the same length.
- Every cell is `'X'` or `'O'`.

## Follow-up

Can you do it without a separate visited grid?
