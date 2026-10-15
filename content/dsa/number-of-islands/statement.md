# Number of Islands

A survey crew sketches a coastline as a list of equal-length text rows. In each row `#` is land and `.` is water. Two land cells belong to the same island when they share a side, that is, when one is directly above, below, left or right of the other. Cells that only touch at a corner are separate islands.

Implement `Archipelago.countIslands(String[] rows)` in `Archipelago.java`. Return the number of islands. The array may be empty.

## Examples

- `rows = {"##..#", "#...#", "..#..", "....#"}` returns `4`: the top-left trio, the right-hand pair in the first two rows, the lone cell in the third row and the lone cell in the last row.
- `rows = {"#.", ".#"}` returns `2`: the two land cells touch only at a corner.
- `rows = {"###", "#.#", "###"}` returns `1`: the ring is one island and the water in its middle does not count.

## Constraints

- 0 <= `rows.length` <= 1,000
- All rows have the same length, between 1 and 1,000.
- Every character is `#` or `.`.
