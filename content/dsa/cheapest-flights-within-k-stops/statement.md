# Cheapest Flights Within K Stops

A small airline serves `n` cities numbered `0` to `n - 1`. Each flight is a one-way connection `{from, to, price}`. A trip from one city to another may change planes along the way; every city passed through between the first takeoff and the last landing is a stop. A direct flight has no stops, a trip of two flights has one stop, and so on.

Implement `Fares.cheapest(int n, int[][] flights, int from, int to, int maxStops)` in `Fares.java`. Return the lowest total price of any trip from `from` to `to` with at most `maxStops` stops, or `-1` if no such trip exists. `from` and `to` are different cities.

## Examples

- `n = 4`, `flights = {{0,1,100},{1,2,100},{2,3,100},{0,2,250},{1,3,300}}`, `from = 0`, `to = 3`, `maxStops = 1` returns `350`: the trip 0, 2, 3 costs 350, and 0, 1, 3 costs 400. The cheapest trip, 0, 1, 2, 3 at 300, needs two stops.
- The same flights with `maxStops = 2` return `300`, and with `maxStops = 0` return `-1`: there is no direct flight from 0 to 3.
- `n = 3`, `flights = {{0,1,50},{0,2,10},{2,1,10}}`, `from = 0`, `to = 1`, `maxStops = 0` returns `50`, because only the direct flight is allowed. With `maxStops = 1` the answer is `20`.

## Constraints

- 2 <= `n` <= 100
- 0 <= `flights.length` <= `n * (n - 1)`; there is at most one flight per ordered pair of cities.
- 0 <= `from`, `to` < `n`, 1 <= price <= 10,000, and 0 <= `maxStops` <= `n`.
