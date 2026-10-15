# Insert Interval

A meeting room keeps its bookings as a list of closed ranges `[start, end]`, sorted by start time, with no two ranges overlapping or touching. A new booking arrives. Add it to the list, and merge it with every existing range it overlaps or touches, so the result again has no two ranges that overlap or touch. Two closed ranges touch when one ends exactly where the other starts.

Implement `Bookings.insert(int[][] booked, int[] added)` in `Bookings.java`. Return the new list, sorted by start. Do not modify `booked`.

## Examples

- `booked = {{9,12},{14,16},{18,20}}`, `added = {11,15}` returns `{{9,16},{18,20}}`: the new range overlaps the first two ranges, and the third stays apart.
- `booked = {{9,12},{14,16},{18,20}}`, `added = {12,13}` returns `{{9,13},{14,16},{18,20}}`: it touches the end of the first range at 12, so they merge.
- `booked = {}`, `added = {5,6}` returns `{{5,6}}`.

## Constraints

- 0 <= `booked.length` <= 100,000
- 0 <= start <= end <= 1,000,000,000 for every range, including `added`.
- `booked` is sorted by start and its ranges neither overlap nor touch.
