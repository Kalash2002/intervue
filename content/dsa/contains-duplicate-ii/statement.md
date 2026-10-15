# Contains Duplicate II

A payments desk logs a numeric code for every order it receives, in the order the orders arrive. An order is suspicious if its code was already seen on an order that arrived at most `gap` places earlier. The code `7` at position 2 and the code `7` at position 5 are 3 places apart.

Implement `Alerts.repeatsNearby(int[] codes, int gap)` in `Alerts.java`. Return `true` if there are two different positions `i < j` with `codes[i] == codes[j]` and `j - i <= gap`, and `false` otherwise. A `gap` of `0` can never match, since two positions are at least 1 apart.

## Examples

- `codes = {4,9,4}`, `gap = 2` returns `true`: the two 4s are 2 places apart.
- `codes = {4,9,4}`, `gap = 1` returns `false`: the only repeat is too far apart.
- `codes = {1,2,3,1,2,3}`, `gap = 2` returns `false`: every value repeats 3 places later, which is more than 2.

## Constraints

- 0 <= `codes.length` <= 100,000
- Every code fits in an `int`; any value, including negatives, may appear.
- 0 <= `gap` <= 100,000
