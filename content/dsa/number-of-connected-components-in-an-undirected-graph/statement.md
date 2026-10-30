# Number of Connected Components in an Undirected Graph

A server room has `n` machines numbered `0` to `n - 1`. Each cable `[a, b]` joins machines `a` and `b`, and data can travel along it in both directions. Two machines are on the same network if data can get from one to the other along any chain of cables. A machine with no cables is a network on its own.

Implement `Clusters.count(int n, int[][] links)` in `Clusters.java`. Return how many separate networks the machines form. The same pair of machines may be joined by more than one cable.

## Example 1

**Input:** `n = 6`, `links = [[0, 4], [4, 2], [1, 5]]`
**Output:** `3`
**Explanation:** Machines 0, 4 and 2 form one network, 1 and 5 another, and machine 3 has no cable, so it is a third.

## Example 2

**Input:** `n = 5`, `links = [[0, 1], [1, 2], [2, 0], [3, 4]]`
**Output:** `2`
**Explanation:** The three cables among 0, 1 and 2 form a loop, which is still one network; 3 and 4 are the other.

## Example 3

**Input:** `n = 4`, `links = []`
**Output:** `4`
**Explanation:** With no cables, every machine is its own network.

## Constraints

- `1 <= n <= 10^5`
- `0 <= links.length <= 2 * 10^5`
- `links[i].length == 2`
- `0 <= links[i][0], links[i][1] < n`
- `links[i][0] != links[i][1]`

## Follow-up

Cables keep being added one at a time. Can you report the count after each one without searching the room again?
