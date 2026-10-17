# Word Search II

You are given a grid of lowercase letters and a list of words. A word is in the grid if you can spell it by starting on any cell holding its first letter and then stepping, one letter at a time, to a cell directly above, below, left or right of the current one. Diagonal steps do not count, and a single trace may not visit the same cell twice.

Implement `WordHunt.findAll(char[][] board, String[] words)` in `WordHunt.java`. Return every word of `words` that can be traced in `board`, each once, in any order. The grid may be changed while you search, as long as it holds its original letters again when the method returns.

## Example 1

**Input:** `board = [["p","l","u","m"],["e","a","r","s"],["n","t","o","k"]]`, `words = ["plum","rot","pear","sort","pan"]`
**Output:** `["plum","pear","rot"]`
**Explanation:** `plum` runs along the top row, `pear` goes down from `p` and then right, and `rot` goes down from `r` and left. In `sort` no `o` touches the `s`, and in `pan` the `a` touches the `p` only corner to corner.

## Example 2

**Input:** `board = [["p","l","u","m"],["e","a","r","s"],["n","t","o","k"]]`, `words = ["ear","earl","era"]`
**Output:** `["ear"]`
**Explanation:** `ear` traces `e`, `a`, `r` along the middle row. `earl` would need an `l` next to that `r`, and `era` an `r` next to the `e`.

## Example 3

**Input:** `board = [["k","k"]]`, `words = ["kk","kkk"]`
**Output:** `["kk"]`
**Explanation:** `kk` can be traced from either cell but is listed once. `kkk` needs three cells and the grid has two.

## Constraints

- `1 <= board.length <= 12`
- `1 <= board[i].length <= 12`
- All rows of `board` have the same length.
- `1 <= words.length <= 3 * 10^4`
- `1 <= words[i].length() <= 15`
- `board` and every word hold only lowercase English letters.
- All words in `words` are different.

## Follow-up

Can you search the grid once for all the words, rather than once per word?
