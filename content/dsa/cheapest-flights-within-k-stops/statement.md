# Cheapest Flights Within K Stops

A small airline serves `n` cities numbered `0` to `n - 1`. Each flight is a one-way connection `{from, to, price}`. A trip from one city to another may change planes along the way; every city passed through between the first takeoff and the last landing is a stop. A direct flight has no stops, a trip of two flights has one stop, and so on, so a trip with at most `maxStops` stops uses at most `maxStops + 1` flights.

Implement `Fares.cheapest(int n, int[][] flights, int from, int to, int maxStops)` in `Fares.java`. Return the lowest total price of any trip from `from` to `to` with at most `maxStops` stops, or `-1` if no such trip exists.

## Example 1

**Input:** `n = 4`, `flights = {{0,1,100},{1,2,100},{2,3,100},{0,2,250},{1,3,300}}`, `from = 0`, `to = 3`, `maxStops = 1`
**Output:** `350`
**Explanation:** With one stop, the trip 0, 2, 3 costs 350 and the trip 0, 1, 3 costs 400. The cheapest trip overall, 0, 1, 2, 3 at 300, needs two stops, so it is not allowed.

## Example 2

**Input:** `n = 4`, `flights = {{0,1,100},{1,2,100},{2,3,100},{0,2,250},{1,3,300}}`, `from = 0`, `to = 3`, `maxStops = 2`
**Output:** `300`
**Explanation:** With two stops the trip 0, 1, 2, 3 is allowed, and at 300 it beats every other trip.

## Example 3

**Input:** `n = 3`, `flights = {{0,1,50},{0,2,10},{2,1,10}}`, `from = 0`, `to = 1`, `maxStops = 0`
**Output:** `50`
**Explanation:** Only a direct flight is allowed, so the 50 flight is the answer. With `maxStops = 1` the trip 0, 2, 1 would cost 20.

## Example 4

**Input:** `n = 4`, `flights = {{0,1,100},{1,2,100},{2,3,100},{0,2,250},{1,3,300}}`, `from = 0`, `to = 3`, `maxStops = 0`
**Output:** `-1`
**Explanation:** There is no direct flight from city 0 to city 3, and no stops are allowed.

## Constraints

- `2 <= n <= 100`
- `0 <= flights.length <= n * (n - 1)`
- There is at most one flight for each ordered pair of cities.
- `0 <= from, to < n`
- `from != to`
- `1 <= price <= 10^4`
- `0 <= maxStops <= n`

## Follow-up

How would you return the route itself, not only the price?
