# Network Delay Time

A sensor network has `n` nodes numbered `0` to `n - 1`. Each link is a one-way connection `{from, to, delay}`: a signal that reaches `from` at time `t` reaches `to` at time `t + delay`. A node forwards the signal on every outgoing link as soon as it first receives it. A signal starts at the node `source` at time 0.

Implement `Signals.timeToReachAll(int n, int[][] links, int source)` in `Signals.java`. Return the time at which the last node receives the signal. If some node can never receive it, return `-1`.

## Examples

- `n = 4`, `links = {{0,1,3},{1,2,4},{0,2,9},{2,3,1}}`, `source = 0` returns `8`: node 2 is reached at 7 through node 1, which beats the direct link at 9, and node 3 follows at 8.
- `n = 3`, `links = {{0,1,2}}`, `source = 0` returns `-1`: nothing leads to node 2.
- `n = 1`, `links = {}`, `source = 0` returns `0`: the signal is already at the only node.

## Constraints

- 1 <= `n` <= 100,000
- 0 <= `links.length` <= 200,000
- 0 <= `from`, `to`, `source` < `n`, and 0 <= `delay` <= 1,000.
- Several links may join the same two nodes.
