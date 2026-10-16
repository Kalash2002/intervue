# Meeting Rooms III

A recording studio has `rooms` rooms, numbered from `0`. Each booking request is `{start, end}`: a session that wants a room from minute `start` up to but not including minute `end`. No two requests have the same `start`. The requests are handled in order of `start`, whatever order they are given in, by these rules:

1. A request takes the free room with the lowest number, if any room is free at its `start`. A room is free again at the minute its current session ends, so a session ending at minute 5 leaves its room free for one starting at minute 5.
2. If every room is busy at its `start`, the request waits until the first room becomes free and then takes it. It keeps its length, so a request for `[2,6)` that actually starts at minute 9 runs `[9,13)`. If several rooms become free at that same minute, it takes the lowest-numbered of them.

Implement `Studios.busiest(int rooms, int[][] bookings)` in `Studios.java`. Return the number (counting from `0`) of the room that hosted the most sessions. If several rooms tie, return the lowest number. A delayed session can finish well after minute `10^9`, so choose your types with that in mind.

## Example 1

**Input:** `rooms = 2`, `bookings = {{0,5},{1,3},{2,4}}`
**Output:** `1`
**Explanation:** The first request takes room 0 and the second room 1. The third finds both busy at minute 2, waits for room 1 at minute 3, and runs `[3,5)` there. Room 1 hosted 2 sessions, room 0 only 1.

## Example 2

**Input:** `rooms = 3`, `bookings = {{0,100},{1,3},{2,4},{5,9},{6,8}}`
**Output:** `1`
**Explanation:** Room 0 stays busy with the long session. Rooms 1 and 2 each host two sessions, and the tie goes to the lower number.

## Example 3

**Input:** `rooms = 4`, `bookings = {{3,4}}`
**Output:** `0`
**Explanation:** One session goes to room 0, the lowest free room, and no other room hosts anything.

## Constraints

- `1 <= rooms <= 10^5`
- `1 <= bookings.length <= 10^5`
- `0 <= start < end <= 10^9`
- All `start` values are different.

## Follow-up

What if a delayed booking should be dropped instead of waiting?
