# Network Delay Time

A sensor network has `n` nodes numbered `0` to `n - 1`; the numbering starts at 0, not 1. Each link is a one-way connection `{from, to, delay}`: a signal that reaches `from` at time `t` reaches `to` at time `t + delay`. A node forwards the signal on every outgoing link as soon as it first receives it. A signal starts at the node `source` at time 0.

Implement `Signals.timeToReachAll(int n, int[][] links, int source)` in `Signals.java`. Return the time at which the last node receives the signal. If some node can never receive it, return `-1`.

## Example 1

**Input:** `n = 4`, `links = {{0,1,3},{1,2,4},{0,2,9},{2,3,1}}`, `source = 0`
**Output:** `8`
**Explanation:** Node 1 is reached at 3. Node 2 is reached at 7 through node 1, which beats the direct link at 9, and node 3 follows at 8, so 8 is when the last node hears the signal.

## Example 2

**Input:** `n = 3`, `links = {{0,1,2}}`, `source = 0`
**Output:** `-1`
**Explanation:** Nothing leads to node 2, so it never receives the signal.

## Example 3

**Input:** `n = 1`, `links = {}`, `source = 0`
**Output:** `0`
**Explanation:** The signal starts at the only node, so it is already there at time 0.

## Constraints

- `1 <= n <= 10^5`
- `0 <= links.length <= 2 * 10^5`
- `0 <= from, to, source < n`
- `0 <= delay <= 1000`
- Several links may join the same two nodes.

## Follow-up

What if some delays could be negative?
