# K Closest Points to Origin

You are given a list of points on a grid, each written `{x, y}`, and a number `k`. Find the `k` points that are closest to the origin `(0, 0)`, measuring ordinary straight-line distance.

Implement `ClosestPoints.kClosest(int[][] points, int k)` in `ClosestPoints.java`. Return an array of the `k` closest points, each as `{x, y}`, in any order; the tests sort your answer before comparing. The same point may appear more than once in the list, and then each copy counts as a separate point. The input is set up so the answer is always unique: the `k`-th closest point is strictly closer than the next one.

Coordinates go up to 100,000 in size, so `x * x + y * y` can reach `2 * 10^10`, well past the range of an `int`.

## Example 1

**Input:** `points = [[3, 4], [-1, 2], [6, -1], [0, -3]]`, `k = 2`
**Output:** `[[-1, 2], [0, -3]]`
**Explanation:** The squared distances are 25, 5, 37 and 9, so the two smallest belong to `[-1, 2]` and `[0, -3]`.

## Example 2

**Input:** `points = [[2, 2], [-2, -2], [1, -3], [5, 0]]`, `k = 3`
**Output:** `[[2, 2], [-2, -2], [1, -3]]`
**Explanation:** The squared distances are 8, 8, 10 and 25. The two points at 8 tie, but both are in the answer, so it is still unique.

## Example 3

**Input:** `points = [[60000, 0], [30000, -30000]]`, `k = 1`
**Output:** `[[30000, -30000]]`
**Explanation:** The squared distances are 3,600,000,000 and 1,800,000,000. In `int` arithmetic the first one wraps around to a negative number and wrongly looks closer.

## Constraints

- `1 <= k <= points.length <= 10^5`
- `-10^5 <= x, y <= 10^5`
- If `k < points.length`, the `k`-th smallest distance is strictly smaller than the `(k+1)`-th.

## Follow-up

What if the points arrive as a stream too large to keep in memory?
