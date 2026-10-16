# Insert Interval

A meeting room keeps its bookings as closed ranges `[start, end]`, listed by start time. No two ranges in the list overlap or touch. A new booking arrives, and it has to go into the list: any existing range that overlaps it or touches it is merged into it. Two closed ranges touch when one ends exactly where the other starts.

Implement `Bookings.insert(int[][] booked, int[] added)` in `Bookings.java`. Return the new list, again sorted by start, with no two ranges overlapping or touching. Do not modify `booked`.

## Example 1

**Input:** `booked = [[9, 12], [14, 16], [18, 20]]`, `added = [11, 15]`
**Output:** `[[9, 16], [18, 20]]`
**Explanation:** The new range overlaps `[9, 12]` and `[14, 16]`, so the three become `[9, 16]`. The range `[18, 20]` starts after 15 and stays apart.

## Example 2

**Input:** `booked = [[9, 12], [14, 16], [18, 20]]`, `added = [12, 13]`
**Output:** `[[9, 13], [14, 16], [18, 20]]`
**Explanation:** The new range starts at 12, exactly where `[9, 12]` ends, so they touch and merge into `[9, 13]`. It ends at 13, before `[14, 16]` starts at 14, so that range stays apart.

## Example 3

**Input:** `booked = []`, `added = [5, 6]`
**Output:** `[[5, 6]]`
**Explanation:** With nothing booked, the new range is the whole list.

## Constraints

- `0 <= booked.length <= 10^5`
- `0 <= start <= end <= 10^9` for every range in `booked` and for `added`
- `booked` is sorted by start, and no two of its ranges overlap or touch.

## Follow-up

How would you do it if the list were stored in a balanced tree?
