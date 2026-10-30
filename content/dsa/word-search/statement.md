# Word Search

You are given a grid of lowercase letters and a word. The word is in the grid if you can spell it by starting on any cell holding its first letter and then stepping, one letter at a time, to a cell directly above, below, left or right of the current one. Diagonal steps do not count, and a single trace may not visit the same cell twice.

Implement `WordSearch.exists(char[][] board, String word)` in `WordSearch.java`. Return `true` if the word can be traced in `board`, otherwise `false`. The grid may be changed while you search, as long as it holds its original letters again when the method returns.

## Example 1

**Input:** `board = [["c","a","t","s"],["o","r","e","m"],["d","i","n","g"]]`, `word = "core"`
**Output:** `true`
**Explanation:** Start on the `c` in the top-left corner, step down to `o`, right to `r` and right again to `e`.

## Example 2

**Input:** `board = [["c","a","t","s"],["o","r","e","m"],["d","i","n","g"]]`, `word = "cart"`
**Output:** `false`
**Explanation:** `c`, `a` and `r` trace fine, but the only `t` touches `r` corner to corner, and a diagonal step is not allowed.

## Example 3

**Input:** `board = [["q"]]`, `word = "qq"`
**Output:** `false`
**Explanation:** The grid has one `q`, and a trace cannot use the same cell twice.

## Constraints

- `1 <= board.length <= 8`
- `1 <= board[i].length <= 8`
- All rows of `board` have the same length.
- `1 <= word.length() <= 16`
- `board` and `word` hold only lowercase English letters.

## Follow-up

How would you cut the search short for a word the grid can never hold?
