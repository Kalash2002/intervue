# Grader notes: Valid Sudoku

Tests cover: a sparse board that is valid (and the same board with a repeat added), a fully solved board, an empty board, a repeat in a row, in a column and in the last row, repeats that sit only in a box (top-left, and two cells of the centre and bottom-right boxes that share no row or column; the trap for a wrong box formula), the same digit placed once in each of nine different boxes (valid, so a solution that is too strict fails), and a board whose first row is complete but cannot be solved (valid, since only repeats are checked).

No naive solution: the board has a fixed size of 81 cells, so there is no meaningfully slower approach to show, and time is not graded separately for this question (ADR 0008).
