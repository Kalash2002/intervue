# Car Pooling

A hospital runs one shuttle bus along a single road. Each ride request is `{riders, boardAt, leaveAt}`: that many people get on at minute `boardAt` and get off at minute `leaveAt`. People who leave at a minute step off before the people boarding at that same minute step on. The shuttle has `seats` seats.

Implement `Shuttle.canCarry(int[][] rides, int seats)` in `Shuttle.java`. Return `true` if the shuttle can honour every request without ever having more than `seats` people aboard, and `false` otherwise. The requests come in no particular order, and with no requests the answer is `true`.

## Examples

- `rides = {{3,2,6},{2,4,9}}`, `seats = 4` returns `false`: from minute 4 to minute 6 both groups are aboard, which is 5 people.
- `rides = {{3,2,6},{2,4,9}}`, `seats = 5` returns `true`: the same trip, and 5 seats just fit.
- `rides = {{3,1,4},{3,4,8}}`, `seats = 3` returns `true`: the first group steps off at minute 4 as the second steps on.

## Constraints

- 0 <= `rides.length` <= 100,000
- 1 <= `riders` <= 1,000 for every request.
- 0 <= `boardAt` < `leaveAt` <= 1,000,000,000 for every request.
- 1 <= `seats` <= 1,000,000,000
