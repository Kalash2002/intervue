# Design Hit Counter

A status page shows how many pings a service received lately. Build the class that keeps that count.

Implement the class `Pings` in `Pings.java`:

- `Pings(int window)` creates an empty counter. A ping at time `t` is counted as recent at time `now` when `now - window < t <= now`; in other words, the last `window` seconds including `now` itself.
- `void ping(int second)` records one ping at the given second.
- `int recent(int second)` returns how many recorded pings are recent at the given second.

Calls come in non-decreasing time order: the `second` of every call is at least the `second` of the call before it, whether that call was a `ping` or a `recent`. Several pings can share a second.

## Examples

With `window = 5`:

- `ping(1)`, `ping(2)`, `ping(2)`, `ping(4)`, then `recent(4)` returns `4`: all four pings are within the last 5 seconds.
- Then `ping(6)` and `recent(6)` returns `4`: the ping at second 1 is now 5 seconds old and has dropped out, leaving the pings at 2, 2, 4 and 6.
- Then `recent(10)` returns `1`: only the ping at second 6 is later than second 5.

## Constraints

- 1 <= `window` <= 1,000,000,000
- 0 <= `second` <= 1,000,000,000 for every call.
- At most 200,000 calls in total.
