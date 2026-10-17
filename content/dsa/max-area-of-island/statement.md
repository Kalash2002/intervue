# Max Area of Island

A survey crew sketches a coastline as a list of equal-length text rows. In each row `#` is land and `.` is water. Two land cells belong to the same island when they share a side, that is, when one is directly above, below, left or right of the other. Cells that only touch at a corner belong to different islands. The area of an island is the number of land cells in it.

Implement `IslandArea.largest(String[] rows)` in `IslandArea.java`. Return the area of the largest island, or `0` when the map has no land. The array may be empty.

## Example 1

**Input:** `rows = {"##..#", ".#..#", "...##", "#...."}`
**Output:** `4`
**Explanation:** The islands are the three cells at the top left, the four cells down the right-hand side that turn left in the third row, and the lone cell in the last row. The largest has 4 cells.

## Example 2

**Input:** `rows = {"#.#", ".#.", "#.#"}`
**Output:** `1`
**Explanation:** The five land cells touch only at corners, so each is an island of one cell.

## Example 3

**Input:** `rows = {"...", "..."}`
**Output:** `0`
**Explanation:** There is no land, so there is no island to measure.

## Constraints

- `0 <= rows.length <= 1000`
- `1 <= rows[i].length <= 1000`
- All rows have the same length.
- Every character of every row is `#` or `.`.

## Follow-up

How would you report the largest island after each cell of land is added?
