# How a strong round goes: Cheapest Flights Within K Stops

## Approach

This is a cheapest-route problem with a second limit, and that limit changes what I have to remember. The cheapest way to reach a city may use many flights, while a dearer way with fewer flights is the only one that can still continue to the destination, so I cannot keep just one price per city and settle it once the way Dijkstra does. Instead I let the trip grow one flight at a time. After round `r`, `best[city]` is the cheapest price to reach that city using at most `r` flights. A round builds the next table from the previous one: copy it, because fewer flights are still allowed, then for every flight try `best[from] + price` against the target city's entry. The trip may use `maxStops + 1` flights, so I run that many rounds.

The detail that causes wrong answers is where a round reads from. If I update `best` in place, a flight relaxed early in a round can feed a flight later in the same round, and one round then spends two flights. So each round reads only the previous table and writes the new one.

```java
static int cheapest(int n, int[][] flights, int from, int to, int maxStops) {
    long[] best = new long[n]; // cheapest known price to each city using the flights allowed so far
    Arrays.fill(best, Long.MAX_VALUE);
    best[from] = 0;
    for (int round = 0; round <= maxStops; round++) { // one more flight allowed per round
        long[] next = best.clone(); // prices carry over: fewer flights are still allowed
        for (int[] flight : flights) {
            if (best[flight[0]] != Long.MAX_VALUE && best[flight[0]] + flight[2] < next[flight[1]]) {
                next[flight[1]] = best[flight[0]] + flight[2]; // read only the previous round
            }
        }
        best = next;
    }
    return best[to] == Long.MAX_VALUE ? -1 : (int) best[to];
}
```

## Complexity

| Approach | Time | Extra space |
| --- | --- | --- |
| Rounds of relaxing every flight (Bellman-Ford with a limit) | O(k x E) | O(V) |
| Dijkstra over (city, flights used) states | O(k x E x log(k x V)) | O(k x V) |
| Try every trip with depth-first search | O(branching to the power k) | O(k) |
| Plain Dijkstra on price, ignoring the limit | O(E log V) | O(V), but gives wrong answers |

V is the number of cities, E the number of flights and k = `maxStops + 1`.

## Where it shows up

`corridor-router` asks for the cheapest route with a limit on hops, and its second phase is exactly this round-by-round table, including the copy of the previous round that stops one round from using two corridors. Its payment network has the same trap: a route settled at its lowest fee may use too many hops to be of any use.

## Follow-up answers

**How would you return the route itself, not only the price?** Keep a second table per round holding the city each price came from. When you finish, follow those links back from the destination through the rounds, and reverse the result. This needs the tables of every round, so space grows to O(k x V).

**What if a stop limit applied but prices could also be negative?** The round-by-round method still works, because it never relies on a price being final. Negative cycles are no longer a problem either, since the number of rounds is fixed. Dijkstra-based versions would break.

**How does the answer change if you must use exactly that many stops?** Do not carry prices over between rounds. Make each new table start as unreachable and fill it only from the previous round's flights, so that after round `r` it describes trips of exactly `r` flights.
