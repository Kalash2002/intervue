# Meeting Rooms II

A coworking space receives a list of bookings, each a pair `[start, end)` of minutes: the room is needed from `start` up to but not including `end`. A booking that ends at minute 11 and another that starts at minute 11 do not overlap, so they can use the same room one after the other. Two bookings in the same room must never overlap.

Implement `Rooms.minRooms(int[][] bookings)` in `Rooms.java`. Return the smallest number of rooms that can hold all the bookings. The bookings come in no particular order, and an empty list needs `0` rooms.

## Example 1

**Input:** `bookings = [[9, 11], [10, 12], [11, 13]]`
**Output:** `2`
**Explanation:** The first and third bookings share a room, because the first ends at 11 as the third starts. The second overlaps both and needs a room of its own.

## Example 2

**Input:** `bookings = [[5, 9], [6, 8], [7, 10]]`
**Output:** `3`
**Explanation:** All three bookings are in progress at minute 7, so no two of them can share a room.

## Example 3

**Input:** `bookings = [[0, 5], [5, 10], [10, 15]]`
**Output:** `1`
**Explanation:** Each booking ends at the minute the next one starts, so one room is enough for all three.

## Constraints

- `0 <= bookings.length <= 10^5`
- `0 <= start < end <= 10^9` for every booking

## Follow-up

How would you also say which room each booking gets?
