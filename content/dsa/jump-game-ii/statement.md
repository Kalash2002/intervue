# Jump Game II

Relay stations stand along a road, numbered from 0. Station `i` can forward a message to any station from 1 up to `range[i]` places further along the road; a range of 0 means it cannot forward at all. A message starts at station 0 and has to reach the last station.

Implement `Relay.fewestHops(int[] range)` in `Relay.java` and return the fewest hops that get the message to the last station.

## Example 1

**Input:** `range = [3,6,0,1,3,0,0,1,0]`
**Output:** `3`
**Explanation:** The message goes 0 → 1 → 7 → 8. Forwarding as far as possible each time goes 0 → 3 → 4 → 7 → 8, and always picking the station with the largest range goes 0 → 1 → 4 → 7 → 8; both take 4 hops.

## Example 2

**Input:** `range = [3,0,0,6,1,0]`
**Output:** `2`
**Explanation:** Stations 1 and 2 cannot forward, so the first hop goes to station 3. Its range of 6 reaches past the last station, so the second hop can land on station 5.

## Example 3

**Input:** `range = [5]`
**Output:** `0`
**Explanation:** The message starts at the last station, so no hop is needed.

## Constraints

- `1 <= range.length <= 10^5`
- `0 <= range[i] <= 10^5`
- The last station can be reached from station 0.

## Follow-up

What if you also had to return the stations the message passes through?
