# Meeting Rooms III

A recording studio has `rooms` rooms, numbered from `0`. Each booking request is `{start, end}`: a session that wants a room from minute `start` up to but not including minute `end`. Every request has a different `start`. The requests are handled in order of `start`, whatever order they are given in, by these rules:

1. A request takes the free room with the lowest number, if any room is free at its `start`. A room is free again at the minute its current session ends, so a session ending at minute 5 leaves its room free for one starting at minute 5.
2. If every room is busy at its `start`, the request waits until the first room becomes free and then takes it. It keeps its length, so a request for `[2,6)` that actually starts at minute 9 runs `[9,13)`. If several rooms become free at that same minute, it takes the lowest-numbered of them.

Implement `Studios.busiest(int rooms, int[][] bookings)` in `Studios.java`. Return the number (counting from `0`) of the room that hosted the most sessions. If several rooms tie, return the lowest number.

## Examples

- `rooms = 2`, `bookings = {{0,5},{1,3},{2,4}}` returns `1`: the first request takes room 0 and the second room 1. The third finds both busy at minute 2, waits for room 1 at minute 3, and runs `[3,5)` there. Room 1 hosted 2 sessions, room 0 only 1.
- `rooms = 3`, `bookings = {{0,100},{1,3},{2,4},{5,9},{6,8}}` returns `1`: room 0 stays busy with the long session; rooms 1 and 2 each host two sessions, and the tie goes to the lower number.
- `rooms = 4`, `bookings = {{3,4}}` returns `0`: one session goes to room 0.

## Constraints

- 1 <= `rooms` <= 100,000
- 1 <= `bookings.length` <= 100,000
- 0 <= `start` < `end` <= 1,000,000,000, and no two requests share a `start`.
- A delayed session can finish well after minute 1,000,000,000; keep that in mind when you choose a type.
