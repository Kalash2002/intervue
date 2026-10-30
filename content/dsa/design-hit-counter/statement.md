# Design Hit Counter

A status page shows how many pings a service received lately. Build the class that keeps that count.

Implement the class `Pings` in `Pings.java`:

- `Pings(int window)` creates an empty counter. A ping at time `t` is counted as recent at time `now` when `now - window < t <= now`; in other words, the last `window` seconds including `now` itself.
- `void ping(int second)` records one ping at the given second.
- `int recent(int second)` returns how many recorded pings are recent at the given second.

Calls come in non-decreasing time order: the `second` of every call is at least the `second` of the call before it, whether that call was a `ping` or a `recent`. Several pings can share a second. In the examples, the output is the list of values returned by the `recent` calls, in order.

## Example 1

**Input:** `Pings(5)`, `ping(1)`, `ping(2)`, `ping(2)`, `ping(4)`, `recent(4)`, `ping(6)`, `recent(6)`, `recent(10)`
**Output:** `[4, 4, 1]`
**Explanation:** At second 4 all four pings are within the last 5 seconds. At second 6 the ping at second 1 is 5 seconds old and has dropped out, leaving the pings at 2, 2, 4 and 6. At second 10 only the ping at 6 is later than second 5.

## Example 2

**Input:** `Pings(1)`, `ping(3)`, `ping(3)`, `recent(3)`, `recent(4)`
**Output:** `[2, 0]`
**Explanation:** A window of 1 second holds only the current second. Both pings count at second 3, and at second 4 they are 1 second old and out.

## Example 3

**Input:** `Pings(3)`, `ping(0)`, `recent(2)`, `recent(3)`
**Output:** `[1, 0]`
**Explanation:** A ping at second 0 is still recent at second 2. At second 3 it is exactly 3 seconds old, which is the `window`, so it is out.

## Constraints

- `1 <= window <= 10^9`
- `0 <= second <= 10^9` for every call
- The total number of calls is at most `2 * 10^5`.

## Follow-up

What if a million pings arrive in the same second?
