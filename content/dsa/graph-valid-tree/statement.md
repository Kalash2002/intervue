# Graph Valid Tree

An irrigation plan has `n` valves numbered `0` to `n - 1` and a list of pipes. Each pipe `[a, b]` joins valves `a` and `b`, and water can flow either way along it. The plan is a tree if water can get from every valve to every other valve and the pipes form no loop, so there is exactly one route between any two valves.

Implement `Pipes.isTree(int n, int[][] links)` in `Pipes.java`. Return `true` if the pipes form a tree over all `n` valves, and `false` otherwise.

## Example 1

**Input:** `n = 5`, `links = [[3, 0], [0, 2], [2, 4], [0, 1]]`
**Output:** `true`
**Explanation:** Every valve is reached from valve 0, and there is no loop: the four pipes branch out from 0 without ever closing back.

## Example 2

**Input:** `n = 5`, `links = [[4, 2], [2, 3], [3, 4], [0, 1]]`
**Output:** `false`
**Explanation:** There are four pipes for five valves, the number a tree needs, but 2, 3 and 4 form a loop and valves 0 and 1 are cut off from the rest.

## Example 3

**Input:** `n = 1`, `links = []`
**Output:** `true`
**Explanation:** A single valve with no pipes is a tree: nothing is cut off and there is no loop.

## Constraints

- `1 <= n <= 10^5`
- `0 <= links.length <= 2 * 10^5`
- `links[i].length == 2`
- `0 <= links[i][0], links[i][1] < n`
- `links[i][0] != links[i][1]`
- No two pipes join the same pair of valves.

## Follow-up

Can you decide it after reading each pipe only once, without building lists of neighbours?
