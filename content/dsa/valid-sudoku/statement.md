# Valid Sudoku

A sudoku board has 9 rows and 9 columns, and is cut into nine boxes of 3 by 3 cells (rows 0 to 2, 3 to 5 and 6 to 8, crossed with the same three groups of columns). Some cells hold a digit from `1` to `9` and the rest are empty.

Implement `Sudoku.isValid(String[] rows)` in `Sudoku.java`. `rows` has 9 strings of 9 characters each, where a character is a digit `1` to `9` or `.` for an empty cell. Return `true` when no digit appears twice in the same row, the same column or the same box, and `false` otherwise. Only the digits already placed are checked: the board does not have to be solvable.

## Examples

- `["1..4..7..", "..6..9..3", ".8..2..5.", "2..5..8..", "..7..1..4", ".9..3..6.", "3..6..9..", "..8..2..5", ".1..4..7."]` returns `true`: no digit repeats in any row, column or box.
- The same board with the first row changed to `"1..4..7.1"` returns `false`: that row now holds two `1`.
- A board that is empty except for a `5` in row 0, column 0 and a `5` in row 1, column 1 returns `false`: the two `5` share the top-left box, though not a row or a column.

## Constraints

- `rows.length == 9`, and every string has length 9.
- Every character is `.` or a digit from `1` to `9`.
