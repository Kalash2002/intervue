# Meeting Rooms

A coworking space has a single meeting room and a list of bookings for it, each a pair `[start, end)` of minutes: the room is needed from `start` up to but not including `end`. Two bookings clash when each starts before the other ends. A booking that ends at minute 45 and another that starts at minute 45 do not clash: they use the room one after the other.

Implement `Rooms.fitInOneRoom(int[][] bookings)` in `Rooms.java`. Return `true` if the one room can hold every booking, that is, if no two bookings clash, and `false` otherwise. The bookings come in no particular order.

## Example 1

**Input:** `bookings = [[30,90],[100,120],[40,50]]`
**Output:** `false`
**Explanation:** `[40,50]` lies inside `[30,90]`. The two are not next to each other in the list, and neither pair of neighbours in the list clashes, so checking only neighbours in the given order would miss it.

## Example 2

**Input:** `bookings = [[14,16],[9,11],[16,18],[11,13]]`
**Output:** `true`
**Explanation:** In order of start the bookings are `[9,11]`, `[11,13]`, `[14,16]` and `[16,18]`, and each starts no earlier than the one before it ends. Two pairs only touch, at minutes 11 and 16.

## Example 3

**Input:** `bookings = [[45,60],[45,46]]`
**Output:** `false`
**Explanation:** Two bookings that start at the same minute always clash, however short one of them is.

## Constraints

- `0 <= bookings.length <= 10^5`
- `0 <= start < end <= 10^9` for every booking

## Follow-up

What if you must also say which two bookings clash?
