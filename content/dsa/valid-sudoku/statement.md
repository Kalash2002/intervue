# Valid Sudoku

A sudoku board has 9 rows and 9 columns, and is cut into nine boxes of 3 by 3 cells (rows 0 to 2, 3 to 5 and 6 to 8, crossed with the same three groups of columns). Some cells hold a digit from `1` to `9` and the rest are empty.

Implement `Sudoku.isValid(String[] rows)` in `Sudoku.java`. `rows` has 9 strings of 9 characters each, where a character is a digit `1` to `9` or `.` for an empty cell. Return `true` when no digit appears twice in the same row, the same column or the same box, and `false` otherwise. Only the digits already placed are checked: the board does not have to be solvable.

## Example 1

**Input:** `rows = ["1..4..7..", "..6..9..3", ".8..2..5.", "2..5..8..", "..7..1..4", ".9..3..6.", "3..6..9..", "..8..2..5", ".1..4..7."]`
**Output:** `true`
**Explanation:** Every row, column and box holds each digit at most once. For example, column 0 holds 1, 2 and 3, and the top-left box holds 1, 6 and 8.

## Example 2

**Input:** `rows = ["1..4..7.1", "..6..9..3", ".8..2..5.", "2..5..8..", "..7..1..4", ".9..3..6.", "3..6..9..", "..8..2..5", ".1..4..7."]`
**Output:** `false`
**Explanation:** This is Example 1 with the last cell of the first row filled with a 1. That row now holds two 1s, at columns 0 and 8.

## Example 3

**Input:** `rows = ["5........", ".5.......", ".........", ".........", ".........", ".........", ".........", ".........", "........."]`
**Output:** `false`
**Explanation:** The two 5s are in different rows and different columns, but both sit in the top-left box, so the box repeats a digit.

## Constraints

- `rows.length == 9`
- `rows[i].length == 9`
- `rows[i][j]` is a digit from `1` to `9` or `.`

## Follow-up

How would the cost grow on a 16 by 16 board with 4 by 4 boxes?
