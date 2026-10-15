# Meeting Rooms II

A coworking space receives a list of bookings, each a pair `[start, end)` of minutes: the room is needed from `start` up to but not including `end`. A booking that ends at minute 11 and another that starts at minute 11 do not overlap, so they can use the same room one after the other. Two bookings in the same room must never overlap.

Implement `Rooms.minRooms(int[][] bookings)` in `Rooms.java`. Return the smallest number of rooms that can hold all the bookings. The bookings come in no particular order, and an empty list needs `0` rooms.

## Examples

- `bookings = {{9,11},{10,12},{11,13}}` returns `2`: the first and third share a room, the second needs its own.
- `bookings = {{1,2},{3,4}}` returns `1`: one room is free again before the next booking.
- `bookings = {{5,9},{6,8},{7,10}}` returns `3`: all three are active at minute 7.

## Constraints

- 0 <= `bookings.length` <= 100,000
- 0 <= start < end <= 1,000,000,000 for every booking.
