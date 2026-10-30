# Detect Squares

A drawing tool lets the user drop points on a grid, and when the cursor rests on a grid point it shows how many squares that point would finish. Build the counter behind it.

Implement the class `SquareFinder` in `SquareFinder.java` with these members.

- `SquareFinder()` makes a finder with no points.
- `void add(int x, int y)` stores the point (x, y). The same point can be added more than once, and every copy is kept.
- `long count(int x, int y)` returns the number of ways to choose three stored points that, together with the query point (x, y), are the four corners of a square with sides parallel to the axes and a side longer than 0. Copies count separately: a corner that was added three times can be chosen in three ways. `count` does not store the query point.

Only squares whose sides run along the grid lines count; a tilted square, or a rectangle that is not a square, does not. The tests store tens of thousands of different points and then call `count` tens of thousands of times, so a `count` that looks at every stored point is too slow.

In the examples, the input is the sequence of calls on a new finder, and the output lists the values returned by the `count` calls, in order.

## Example 1

**Input:** `["SquareFinder()", "add(6, 7)", "add(10, 7)", "add(10, 3)", "add(2, 7)", "add(2, 3)", "count(6, 3)", "count(6, 7)"]`
**Output:** `[2, 0]`
**Explanation:** (6, 3) finishes a square of side 4 on each side: with (6, 7), (10, 7) and (10, 3) to the right, and with (6, 7), (2, 7) and (2, 3) to the left. For (6, 7), no stored point shares its column except a copy of itself, and a side of 0 is no square.

## Example 2

**Input:** `["SquareFinder()", "add(0, 5)", "add(3, 5)", "add(3, 5)", "add(0, 8)", "add(0, 8)", "count(3, 8)"]`
**Output:** `[4]`
**Explanation:** The square has the corners (3, 8), (3, 5), (0, 5) and (0, 8). (3, 5) and (0, 8) were each added twice, so there are 2 × 2 × 1 = 4 ways to choose the other three corners.

## Example 3

**Input:** `["SquareFinder()", "count(7, 7)", "add(5, 0)", "add(10, 5)", "add(5, 10)", "count(0, 5)"]`
**Output:** `[0, 0]`
**Explanation:** Nothing is stored at the first `count`. Then (0, 5), (5, 0), (10, 5) and (5, 10) are the corners of a square, but it is tilted by 45 degrees, so it does not count.

## Constraints

- `0 <= x, y <= 1000`
- `1 <= number of add and count calls on one finder <= 2 * 10^5`

## Follow-up

What if the coordinates could be as large as 10^9?
