# Min Cost to Connect All Points

Sensors stand on a field at whole-number grid points. They must all be joined into one wired network, where a cable runs between two sensors and data can pass along any chain of cables. Cable is laid along the grid lines, so a cable between `[x1, y1]` and `[x2, y2]` has length `|x1 - x2| + |y1 - y2|`. Cables only meet at sensors.

Implement `Field.minCable(int[][] points)` in `Field.java`, where `points[i] = [x, y]` is the position of sensor `i`. Return the smallest total cable length that joins every sensor to every other, directly or through other sensors.

## Example 1

**Input:** `points = [[0, 0], [3, 1], [1, 4], [5, 5]]`
**Output:** `14`
**Explanation:** Join `[3, 1]` to `[0, 0]` (length 4), `[1, 4]` to `[0, 0]` (5) and `[5, 5]` to `[1, 4]` (5). No set of cables that joins all four is shorter.

## Example 2

**Input:** `points = [[2, -3], [-1, 2], [4, 4]]`
**Output:** `15`
**Explanation:** The three possible cables have lengths 8, 9 and 7. The two shortest, 7 and 8, already join all three sensors.

## Example 3

**Input:** `points = [[7, 7]]`
**Output:** `0`
**Explanation:** A single sensor needs no cable.

## Constraints

- `1 <= points.length <= 2000`
- `points[i].length == 2`
- `-10^6 <= points[i][0], points[i][1] <= 10^6`
- No two sensors share a position.

## Follow-up

Every pair of sensors can be joined, so there are about two million possible cables. Can you avoid listing and sorting all of them?
