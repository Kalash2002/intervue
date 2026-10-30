# Rotting Oranges

A crate of oranges is packed as a grid of slots. Each slot holds `0` when it is empty, `1` when it holds a fresh orange and `2` when it holds a mouldy one. Once a minute, every fresh orange that shares a side with a mouldy orange turns mouldy itself, all at the same moment, so an orange that turns this minute spreads mould only from the next minute on. Mould never crosses an empty slot and never passes between oranges that touch only at a corner.

Implement `Spoilage.minutesUntilSpoiled(int[][] grid)` in `Spoilage.java`. Return the number of minutes until no fresh orange is left, or `-1` if some fresh orange is never reached. A crate with no fresh orange at the start needs `0` minutes. Do not change `grid`.

## Example 1

**Input:** `grid = [[1,1,0,2],[0,1,1,1],[1,1,0,0]]`
**Output:** `5`
**Explanation:** The mould starts at the top right corner and reaches the orange below it in minute 1, then walks left along the middle row in minutes 2 and 3, reaches the top and bottom oranges of the second column in minute 4 and the last two in the first column in minute 5.

## Example 2

**Input:** `grid = [[2,1,1,1,1,2]]`
**Output:** `2`
**Explanation:** Mould spreads from both ends at once, so the two oranges in the middle turn in minute 2.

## Example 3

**Input:** `grid = [[1,0],[0,2]]`
**Output:** `-1`
**Explanation:** The fresh orange touches the mouldy one only at a corner, and its side neighbours are empty, so it stays fresh for ever.

## Example 4

**Input:** `grid = [[0,0],[0,0]]`
**Output:** `0`
**Explanation:** There is no fresh orange to wait for, so no time passes.

## Constraints

- `1 <= grid.length <= 1000`
- `1 <= grid[i].length <= 1000`
- All rows have the same length.
- Every `grid[i][j]` is `0`, `1` or `2`.

## Follow-up

Can you avoid re-scanning the whole crate every minute?
