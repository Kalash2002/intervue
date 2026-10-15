# Find if Path Exists in Graph

A tram network has `n` stations numbered `0` to `n - 1`. Each connector is a pair `{a, b}` meaning trams run between stations `a` and `b` in both directions. You may change trams at any station.

Implement `Routes.connected(int n, int[][] links, int from, int to)` in `Routes.java`. Return `true` if a rider can get from station `from` to station `to` using any number of connectors, and `false` otherwise. A rider is always at their own station, so `from == to` is `true` even with no connectors at all.

## Examples

- `n = 5`, `links = {{0,1},{1,2},{3,4}}`, `from = 0`, `to = 2` returns `true`: via station 1.
- Same network, `from = 0`, `to = 4` returns `false`: stations 3 and 4 form a separate island.
- `n = 1`, `links = {}`, `from = 0`, `to = 0` returns `true`: the rider is already there.

## Constraints

- 1 <= `n` <= 100,000
- 0 <= `links.length` <= 200,000
- 0 <= `a`, `b` < `n` and `a != b` for every connector; the same pair may be listed more than once.
- 0 <= `from`, `to` < `n`
