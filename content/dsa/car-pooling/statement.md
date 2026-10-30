# Car Pooling

A hospital runs one shuttle bus along a single road. Each ride request is `[riders, boardAt, leaveAt]`: that many people get on at minute `boardAt` and get off at minute `leaveAt`. People who leave at a minute step off before the people boarding at that same minute step on. The shuttle has `seats` seats.

Implement `Shuttle.canCarry(int[][] rides, int seats)` in `Shuttle.java`. Return `true` if the shuttle can honour every request without ever having more than `seats` people aboard, and `false` otherwise. The requests come in no particular order, and with no requests the answer is `true`.

## Example 1

**Input:** `rides = [[3, 2, 6], [2, 4, 9]]`, `seats = 4`
**Output:** `false`
**Explanation:** From minute 4 to minute 6 both groups are aboard, which is 5 people, one more than the seats.

## Example 2

**Input:** `rides = [[3, 2, 6], [2, 4, 9]]`, `seats = 5`
**Output:** `true`
**Explanation:** The same trip with 5 seats: the peak of 5 people just fits.

## Example 3

**Input:** `rides = [[3, 1, 4], [3, 4, 8]]`, `seats = 3`
**Output:** `true`
**Explanation:** The first group steps off at minute 4 as the second steps on, so there are never more than 3 aboard.

## Constraints

- `0 <= rides.length <= 10^5`
- `1 <= riders <= 1000` for every request
- `0 <= boardAt < leaveAt <= 10^9` for every request
- `1 <= seats <= 10^9`

## Follow-up

How would you report the first minute at which the shuttle is over capacity?
