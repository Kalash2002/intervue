# Number of Recent Calls

A service wants to know how busy it is right now. It records each incoming call with the millisecond at which it arrived, and after every call it asks: how many calls, this one included, arrived within the last `window` milliseconds?

Implement the class `CallCounter` in `CallCounter.java`:

- `CallCounter(int window)` creates a counter that looks back `window` milliseconds.
- `int ping(int t)` records a call arriving at millisecond `t` and returns how many recorded calls, including this one, have an arrival time in the closed range from `t - window` to `t`.

Both ends of the range count: a call that arrived exactly `window` milliseconds ago is still inside.

## Example 1

**Input:** `window = 10`, calls at `4, 9, 14, 15, 25`
**Output:** `1, 2, 3, 3, 2`
**Explanation:** At 14 the range is 4 to 14, so the call at 4 still counts. At 15 it is 5 to 15, so it has dropped out. At 25 the range is 15 to 25, which holds only the calls at 15 and 25.

## Example 2

**Input:** `window = 1`, calls at `1, 2, 3`
**Output:** `1, 2, 2`
**Explanation:** At 3 the range is 2 to 3, so the call at 1 is out.

## Example 3

**Input:** `window = 5`, calls at `10, 15, 16`
**Output:** `1, 2, 2`
**Explanation:** The call at 10 is exactly 5 milliseconds old at 15, so it still counts. At 16 the range is 11 to 16 and it has dropped out.

## Constraints

- `1 <= window <= 10^9`
- `1 <= t <= 10^9`, and `t` is larger than in the previous call to `ping` on the same counter.
- At most `2 * 10^5` calls per counter.

## Follow-up

What if calls can arrive out of order?
