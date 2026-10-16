# Find if Path Exists in Graph

A tram network has `n` stations numbered `0` to `n - 1`. Each connector is a pair `[a, b]` meaning trams run between stations `a` and `b` in both directions. You may change trams at any station.

Implement `Routes.connected(int n, int[][] links, int from, int to)` in `Routes.java`. Return `true` if a rider can get from station `from` to station `to` using any number of connectors, and `false` otherwise. A rider is always at their own station, so `from == to` is `true` even with no connectors at all.

## Example 1

**Input:** `n = 5`, `links = [[0, 1], [1, 2], [3, 4]]`, `from = 0`, `to = 2`
**Output:** `true`
**Explanation:** A rider goes from station 0 to 1 and then from 1 to 2.

## Example 2

**Input:** `n = 5`, `links = [[0, 1], [1, 2], [3, 4]]`, `from = 0`, `to = 4`
**Output:** `false`
**Explanation:** Stations 3 and 4 are joined only to each other, a separate island that no ride from 0 can reach.

## Example 3

**Input:** `n = 1`, `links = []`, `from = 0`, `to = 0`
**Output:** `true`
**Explanation:** The rider is already at the destination, and no connector is needed.

## Constraints

- `1 <= n <= 10^5`
- `0 <= links.length <= 2 * 10^5`
- `0 <= links[i][0], links[i][1] < n`
- `links[i][0] != links[i][1]`, and the same pair may be listed more than once.
- `0 <= from, to < n`

## Follow-up

How would you answer many from-to questions on the same network?
