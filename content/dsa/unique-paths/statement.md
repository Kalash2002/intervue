# Unique Paths

A parcel crosses a sorting floor of `rows` by `cols` square cells. It enters at the top-left cell and must reach the bottom-right cell, and the belts under it can only push it one cell to the right or one cell down at a time.

Implement `SortingFloor.routes(int rows, int cols)` in `SortingFloor.java` and return how many different routes the parcel can take. Two routes are different when they pass through a different sequence of cells. On a floor of a single cell the parcel starts on the goal, and staying put counts as one route.

## Example 1

**Input:** `rows = 4`, `cols = 5`
**Output:** `35`
**Explanation:** Every route makes 3 moves down and 4 moves to the right in some order, for example right, down, right, right, down, right, down. There are 35 different orders.

## Example 2

**Input:** `rows = 6`, `cols = 4`
**Output:** `56`
**Explanation:** Here every route makes 5 moves down and 3 to the right. A floor of 4 rows by 6 columns has the same 56 routes, turned on their side.

## Example 3

**Input:** `rows = 1`, `cols = 1`
**Output:** `1`
**Explanation:** The parcel enters on the goal cell, so the only route is the one with no moves.

## Constraints

- `1 <= rows <= 100`
- `1 <= cols <= 100`
- The number of routes is at most `2 * 10^9`, so it fits an `int`.

## Follow-up

What if some cells were blocked?
