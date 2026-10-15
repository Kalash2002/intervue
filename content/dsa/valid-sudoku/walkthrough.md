# How a strong round goes: Valid Sudoku

## Approach

The board is a fixed 9 by 9 grid, so the question is only about bookkeeping: three kinds of group, each digit allowed once per group. I visit each cell once. For a digit, I check whether that digit has already been seen in this row, this column or this box, and if so the board is invalid. If not, I note it in all three. The box number comes from integer division: `row / 3 * 3 + column / 3` runs from 0 to 8 across the nine boxes. The easy mistake is a wrong box formula, such as leaving out the `* 3`, which makes two boxes share a number; the tests put repeats that sit only in a box, with different rows and columns, to catch it. The board does not need to be solvable, so I check nothing but the repeats of digits that are already there.

```java
static boolean isValid(String[] rows) {
    boolean[][] inRow = new boolean[9][10]; // inRow[r][d]: digit d already seen in row r
    boolean[][] inColumn = new boolean[9][10];
    boolean[][] inBox = new boolean[9][10];
    for (int r = 0; r < 9; r++) {
        for (int c = 0; c < 9; c++) {
            char ch = rows[r].charAt(c);
            if (ch == '.') {
                continue; // empty cells break no rule
            }
            int d = ch - '0';
            int box = r / 3 * 3 + c / 3; // boxes numbered 0..8, left to right, top to bottom
            if (inRow[r][d] || inColumn[c][d] || inBox[box][d]) {
                return false; // a repeat in some group
            }
            inRow[r][d] = true;
            inColumn[c][d] = true;
            inBox[box][d] = true;
        }
    }
    return true;
}
```

## Complexity

| Approach | Time | Extra space |
| --- | --- | --- |
| Compare each cell with the other cells of its row, column and box | O(1) for the fixed 81 cells, about 20 comparisons per cell | O(1) |
| One pass with seen records for rows, columns and boxes | O(1) for the fixed 81 cells, a few lookups per cell | O(1) |

The board has a fixed size, so both are constant in the strict sense. On an N by N board, with N a perfect square, the seen-records pass costs O(N squared) and the comparison costs O(N cubed).

## Where it shows up

Checking that no value repeats inside a set of overlapping groups is common in data validation: a timetable where no teacher, room or class can be booked twice in the same slot, or a form import that must keep an identifier unique within each of several scopes. The per-group "seen" table is the usual tool.

## Follow-up answers

**How would you check the whole board in a single pass over its cells?** The reference already does it: each cell is visited once and checked against the three records at once.

**How would you store the seen digits in one integer per row, column and box?** Use bit `d` of the integer for digit `d`. Testing is `(mask >> d) & 1`, and marking is `mask |= 1 << d`. That replaces the 27 small arrays with 27 integers.

**How would you go on to decide whether the board can be completed?** That is a search with backtracking: choose an empty cell, try each digit that the three records allow, recurse, and undo the choice on a dead end. Keeping the records up to date as you place and remove digits is exactly the bookkeeping from this question.
