# Number of Islands

A survey crew sketches a coastline as a list of equal-length text rows. In each row `#` is land and `.` is water. Two land cells belong to the same island when they share a side, that is, when one is directly above, below, left or right of the other. Cells that only touch at a corner are separate islands.

Implement `Archipelago.countIslands(String[] rows)` in `Archipelago.java`. Return the number of islands. The array may be empty.

## Example 1

**Input:** `rows = {"##..#", "#...#", "..#..", "....#"}`
**Output:** `4`
**Explanation:** The islands are the top-left trio, the right-hand pair in the first two rows, the lone cell in the third row and the lone cell in the last row.

## Example 2

**Input:** `rows = {"#.", ".#"}`
**Output:** `2`
**Explanation:** The two land cells touch only at a corner, which does not connect them.

## Example 3

**Input:** `rows = {"###", "#.#", "###"}`
**Output:** `1`
**Explanation:** The ring is one island, and the water in its middle does not count.

## Example 4

**Input:** `rows = {}`
**Output:** `0`
**Explanation:** With no rows there is no land.

## Constraints

- `0 <= rows.length <= 1000`
- `1 <= rows[i].length <= 1000`
- All rows have the same length.
- Every character of every row is `#` or `.`.

## Follow-up

How would you also report the size of the largest island?
