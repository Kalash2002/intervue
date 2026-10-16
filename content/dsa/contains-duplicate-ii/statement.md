# Contains Duplicate II

A payments desk logs a numeric code for every order it receives, in the order the orders arrive. An order is suspicious if its code was already seen on an order that arrived at most `gap` places earlier. The code `7` at position 2 and the code `7` at position 5 are 3 places apart.

Implement `Alerts.repeatsNearby(int[] codes, int gap)` in `Alerts.java`. Return `true` if there are two different positions `i < j` with `codes[i] == codes[j]` and `j - i <= gap`, and `false` otherwise.

## Example 1

**Input:** `codes = [4, 9, 4]`, `gap = 2`
**Output:** `true`
**Explanation:** The two 4s are at positions 0 and 2, which are 2 places apart, within the gap of 2.

## Example 2

**Input:** `codes = [9, 4, 7, 9, 4]`, `gap = 2`
**Output:** `false`
**Explanation:** Both 9 and 4 repeat, but each repeat is 3 places after the first, which is more than 2.

## Example 3

**Input:** `codes = [2, 5, 5]`, `gap = 0`
**Output:** `false`
**Explanation:** The two 5s are next to each other, 1 place apart, and a gap of 0 allows no distance at all. Two different positions are always at least 1 apart.

## Constraints

- `0 <= codes.length <= 10^5`
- `-2^31 <= codes[i] <= 2^31 - 1`
- `0 <= gap <= 10^5`

## Follow-up

How would you do it keeping only the last `gap` values in memory?
