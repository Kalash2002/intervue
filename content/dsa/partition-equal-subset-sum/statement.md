# Partition Equal Subset Sum

Two hikers share the gear for a trip. Item `i` weighs `weights[i]`, every item goes into exactly one of the two packs, and items cannot be split. The hikers want both packs to weigh exactly the same.

Implement `Packs.canBalance(int[] weights)` in `Packs.java` and return `true` when the items can be shared out so that the two packs weigh the same.

## Example 1

**Input:** `weights = [3,8,4,6,1]`
**Output:** `true`
**Explanation:** The total is 22, so each pack must weigh 11: 3 + 8 in one, 4 + 6 + 1 in the other.

## Example 2

**Input:** `weights = [6,11,4,9,8]`
**Output:** `true`
**Explanation:** 11 + 8 against 9 + 6 + 4, 19 each. Handing each item, heaviest first, to the lighter pack ends at 21 against 17.

## Example 3

**Input:** `weights = [3,2,6]`
**Output:** `false`
**Explanation:** The total, 11, is odd, so it cannot be halved, even though 3 + 2 makes 5, half of 11 rounded down.

## Example 4

**Input:** `weights = [6]`
**Output:** `false`
**Explanation:** A single item cannot be split, so one pack would stay empty.

## Constraints

- `1 <= weights.length <= 200`
- `1 <= weights[i] <= 100`

## Follow-up

What if the packs only had to weigh as close to each other as possible?
