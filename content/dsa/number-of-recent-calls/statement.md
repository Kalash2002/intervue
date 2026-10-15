# Number of Recent Calls

A service wants to know how busy it is right now. It records each incoming call with the millisecond at which it arrived, and after every call it asks: how many calls, this one included, arrived within the last `window` milliseconds?

Implement the class `CallCounter` in `CallCounter.java`:

- `CallCounter(int window)` creates a counter that looks back `window` milliseconds.
- `int ping(int t)` records a call arriving at millisecond `t` and returns how many recorded calls, including this one, have an arrival time in the closed range from `t - window` to `t`.

Both ends of the range count: a call that arrived exactly `window` milliseconds ago is still inside. Every call arrives strictly later than the one before it.

## Examples

- `window = 10`, calls at `4, 9, 14, 15, 25` return `1, 2, 3, 3, 2`. At 14 the range is 4 to 14, so the call at 4 still counts. At 15 it is 5 to 15, so it has dropped out.
- `window = 1`, calls at `1, 2, 3` return `1, 2, 2`: at 3 the range is 2 to 3.
- `window = 5`, calls at `100, 103, 108, 109` return `1, 2, 2, 2`: at 108 the range is 103 to 108, and at 109 it is 104 to 109.

## Constraints

- 1 <= `window` <= 1,000,000,000
- 1 <= `t` <= 1,000,000,000, strictly increasing from one call to the next.
- At most 200,000 calls per counter.
